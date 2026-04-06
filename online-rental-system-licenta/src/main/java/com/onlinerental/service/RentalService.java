package com.onlinerental.service;

import com.onlinerental.domain.DeliveryStatus;
import com.onlinerental.domain.DeliveryType;
import com.onlinerental.domain.InventoryStatus;
import com.onlinerental.domain.InventoryUnit;
import com.onlinerental.domain.ItemCondition;
import com.onlinerental.domain.Rental;
import com.onlinerental.domain.RentalStatus;
import com.onlinerental.domain.User;
import com.onlinerental.repository.InventoryUnitRepository;
import com.onlinerental.repository.RentalRepository;
import com.onlinerental.repository.UserRepository;
import com.onlinerental.util.PricingUtil;
import com.onlinerental.web.DtoMapper;
import com.onlinerental.web.dto.ApiResponse;
import com.onlinerental.web.dto.ConditionCheckRequest;
import com.onlinerental.web.dto.RentalDto;
import com.onlinerental.web.dto.RentalRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RentalService {

    private final RentalRepository rentalRepository;
    private final InventoryUnitRepository inventoryUnitRepository;
    private final UserRepository userRepository;

    @Transactional
    public RentalDto create(RentalRequest req, String username) {
        User user = userRepository.findByUsername(username).orElseThrow();
        InventoryUnit inv = inventoryUnitRepository.findById(req.inventoryId())
                .orElseThrow(() -> new IllegalArgumentException("Unitate inventar inexistentă"));
        if (inv.getStatus() != InventoryStatus.AVAILABLE) {
            throw new IllegalArgumentException("Unitatea nu este disponibilă");
        }
        if (!req.startDate().isBefore(req.endDate())) {
            throw new IllegalArgumentException("Data de început trebuie să fie înainte de data de sfârșit");
        }
        var product = inv.getProduct();
        int days = PricingUtil.rentalDaysInclusive(req.startDate(), req.endDate());
        BigDecimal total = PricingUtil.totalPrice(product.getDailyPrice(), days, product.getDiscountPercent());
        BigDecimal deposit = PricingUtil.depositFor(total);

        Rental.RentalBuilder b = Rental.builder()
                .user(user)
                .inventory(inv)
                .startDate(req.startDate())
                .endDate(req.endDate())
                .status(RentalStatus.PENDING)
                .totalPrice(total)
                .depositAmount(deposit)
                .itemCondition(ItemCondition.PENDING_CHECK);

        if (req.deliveryType() != null) {
            DeliveryType dt = DeliveryType.valueOf(req.deliveryType());
            b.deliveryType(dt);
            if (dt == DeliveryType.DELIVERY) {
                b.deliveryAddress(req.deliveryAddress());
                b.deliveryPhone(req.deliveryPhone());
                b.deliveryStatus(DeliveryStatus.PENDING);
                b.twoDayDelivery(Boolean.TRUE.equals(req.twoDayDelivery()));
                int add = Boolean.TRUE.equals(req.twoDayDelivery()) ? 2 : 5;
                b.estimatedDeliveryDate(req.startDate().minusDays(1).isAfter(LocalDate.now())
                        ? req.startDate().plusDays(add)
                        : LocalDate.now().plusDays(add));
            } else {
                b.pickupDate(req.startDate());
            }
        }

        Rental saved = rentalRepository.save(b.build());
        inv.setStatus(InventoryStatus.ON_RENTAL);
        inv.setUpdatedAt(java.time.Instant.now());
        inventoryUnitRepository.save(inv);
        return loadDto(saved.getId(), user, false);
    }

    public List<RentalDto> myRentals(String username) {
        User u = userRepository.findByUsername(username).orElseThrow();
        return rentalRepository.findByUserOrderByCreatedAtDesc(u).stream()
                .map(r -> loadDto(r.getId(), u, false))
                .toList();
    }

    public List<RentalDto> allRentals() {
        return rentalRepository.findAllWithDetails().stream()
                .map(r -> DtoMapper.toRentalDto(r))
                .toList();
    }

    public RentalDto getById(Long id, User actor) {
        if (isAdmin(actor)) {
            Rental r = rentalRepository.findByIdWithDetails(id)
                    .orElseThrow(() -> new IllegalArgumentException("Închiriere inexistentă"));
            return DtoMapper.toRentalDto(r);
        }
        User u = userRepository.findByUsername(actor.getUsername()).orElseThrow();
        Rental r = rentalRepository.findByIdAndUserWithDetails(id, u)
                .orElseThrow(() -> new IllegalArgumentException("Închiriere inexistentă"));
        return DtoMapper.toRentalDto(r);
    }

    @Transactional
    public ApiResponse updateStatus(Long id, String newStatus, User actor) {
        if (!isAdmin(actor)) {
            throw new IllegalStateException("Acces interzis");
        }
        RentalStatus st = RentalStatus.valueOf(newStatus);
        Rental r = rentalRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new IllegalArgumentException("Închiriere inexistentă"));
        r.setStatus(st);
        if (st == RentalStatus.ACTIVE) {
            r.setDeliveryStatus(DeliveryStatus.DELIVERED);
            r.setActualDeliveryDate(LocalDate.now());
        }
        if (st == RentalStatus.CANCELED || st == RentalStatus.COMPLETED || st == RentalStatus.RETURNED) {
            releaseInventoryIfDone(r, st);
        }
        return ApiResponse.ok("Status actualizat");
    }

    @Transactional
    public ApiResponse delete(Long id, User actor) {
        if (!isAdmin(actor)) {
            throw new IllegalStateException("Acces interzis");
        }
        Rental r = rentalRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new IllegalArgumentException("Închiriere inexistentă"));
        if (r.getStatus() != RentalStatus.PENDING && r.getStatus() != RentalStatus.CANCELED) {
            throw new IllegalArgumentException("Ștergerea este permisă doar pentru rezervări în așteptare sau anulate");
        }
        freeInventory(r);
        rentalRepository.delete(r);
        return ApiResponse.ok("Șters");
    }

    @Transactional
    public ApiResponse checkCondition(Long id, ConditionCheckRequest req, User actor) {
        if (!isAdmin(actor)) {
            throw new IllegalStateException("Acces interzis");
        }
        Rental r = rentalRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new IllegalArgumentException("Închiriere inexistentă"));
        r.setItemCondition(ItemCondition.valueOf(req.condition()));
        r.setConditionNotes(req.notes());
        return ApiResponse.ok("Stare înregistrată");
    }

    @Transactional
    public ApiResponse generateAwb(Long id, User actor) {
        if (!isAdmin(actor)) {
            throw new IllegalStateException("Acces interzis");
        }
        Rental r = rentalRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new IllegalArgumentException("Închiriere inexistentă"));
        r.setAwbNumber("AWB-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        r.setDeliveryStatus(DeliveryStatus.IN_TRANSIT);
        return ApiResponse.ok("AWB generat");
    }

    private RentalDto loadDto(Long id, User user, boolean admin) {
        Rental r = admin
                ? rentalRepository.findByIdWithDetails(id).orElseThrow()
                : rentalRepository.findByIdAndUserWithDetails(id, user).orElseThrow();
        return DtoMapper.toRentalDto(r);
    }

    private boolean isAdmin(User actor) {
        return actor.getRoles().contains("ROLE_ADMIN") || actor.getRoles().contains("ROLE_SUPEROWNER");
    }

    private void releaseInventoryIfDone(Rental r, RentalStatus st) {
        if (st == RentalStatus.COMPLETED || st == RentalStatus.CANCELED || st == RentalStatus.RETURNED) {
            freeInventory(r);
        }
    }

    private void freeInventory(Rental r) {
        InventoryUnit inv = r.getInventory();
        inv.setStatus(InventoryStatus.AVAILABLE);
        inv.setUpdatedAt(java.time.Instant.now());
        inventoryUnitRepository.save(inv);
    }

    public Rental requireRentalForPayment(Long id, String username) {
        User u = userRepository.findByUsername(username).orElseThrow();
        return rentalRepository.findByIdAndUserWithDetails(id, u)
                .orElseThrow(() -> new IllegalArgumentException("Închiriere inexistentă"));
    }

    public Rental requireRentalByPaymentIntent(String paymentIntentId) {
        return rentalRepository.findByStripePaymentIntentId(paymentIntentId)
                .orElseThrow(() -> new IllegalArgumentException("Plată necunoscută"));
    }

    @Transactional
    public void attachPaymentIntent(Long rentalId, String paymentIntentId) {
        Rental r = rentalRepository.findById(rentalId).orElseThrow();
        r.setStripePaymentIntentId(paymentIntentId);
    }

    @Transactional
    public void confirmPaid(String paymentIntentId) {
        Rental r = requireRentalByPaymentIntent(paymentIntentId);
        if (r.getStatus() != RentalStatus.PENDING) {
            throw new IllegalArgumentException("Rezervarea nu așteaptă plată");
        }
        r.setStatus(RentalStatus.CONFIRMED);
    }
}
