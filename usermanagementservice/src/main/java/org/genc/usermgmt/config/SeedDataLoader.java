package org.genc.usermgmt.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.genc.usermgmt.entity.Role;
import org.genc.usermgmt.entity.User;
import org.genc.usermgmt.enums.RoleType;
import org.genc.usermgmt.repo.RoleRepository;
import org.genc.usermgmt.repo.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class SeedDataLoader implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        log.info("========== Starting SeedDataLoader at {} ==========", LocalDateTime.now());

        try {
            // 1. Seed the three roles (BUYER, SELLER, ADMIN)
            seedRoles();

            // 2. Create default ADMIN user
            seedAdminUser();

            // 3. Create sample users for testing
            seedSampleUsers();

            log.info("========== SeedDataLoader completed successfully at {} ==========", LocalDateTime.now());
        } catch (Exception e) {
            log.error("Error in SeedDataLoader: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Seed initial roles: BUYER, SELLER, ADMIN
     */
    private void seedRoles() {
        log.info("Seeding roles...");

        // BUYER role
        if (roleRepository.findByRoleName(RoleType.BUYER.name()).isEmpty()) {
            Role buyerRole = new Role();
            buyerRole.setRoleName(RoleType.BUYER.name());
            roleRepository.save(buyerRole);
            log.info("Created BUYER role");
        } else {
            log.info("BUYER role already exists");
        }

        // SELLER role
        if (roleRepository.findByRoleName(RoleType.SELLER.name()).isEmpty()) {
            Role sellerRole = new Role();
            sellerRole.setRoleName(RoleType.SELLER.name());
            roleRepository.save(sellerRole);
            log.info("Created SELLER role");
        } else {
            log.info("SELLER role already exists");
        }

        // ADMIN role
        if (roleRepository.findByRoleName(RoleType.ADMIN.name()).isEmpty()) {
            Role adminRole = new Role();
            adminRole.setRoleName(RoleType.ADMIN.name());
            roleRepository.save(adminRole);
            log.info("Created ADMIN role");
        } else {
            log.info("ADMIN role already exists");
        }
    }

    /**
     * Create default ADMIN user for system administration
     * Credentials: username=admin, password=admin123
     */
    private void seedAdminUser() {
        log.info("Seeding admin user...");

        // Check if admin user already exists
        if (userRepository.findByUsername("admin").isPresent()) {
            log.info("Admin user already exists");
            return;
        }

        // Fetch the ADMIN role
        Role adminRole = roleRepository.findByRoleName(RoleType.ADMIN.name())
                .orElseThrow(() -> {
                    log.error("ADMIN role not found");
                    return new RuntimeException("ADMIN role not found");
                });

        // Create the admin user
        User adminUser = new User();
        adminUser.setUsername("admin");
        adminUser.setPassword(passwordEncoder.encode("admin123"));
        adminUser.setEmail("admin@onepiece-auction.com");
        adminUser.setFirstName("System");
        adminUser.setLastName("Administrator");
        adminUser.setContact(9999999999L);

        // Add the ADMIN role
        adminUser.getRoles().add(adminRole);

        userRepository.save(adminUser);
        log.info("✓ Created default ADMIN user - Username: admin");
    }

    /**
     * Create sample users for testing (optional)
     */
    private void seedSampleUsers() {
        log.info("Seeding sample users...");

        // Get BUYER and SELLER roles
        Role buyerRole = roleRepository.findByRoleName(RoleType.BUYER.name())
                .orElseThrow(() -> new RuntimeException("BUYER role not found"));
        Role sellerRole = roleRepository.findByRoleName(RoleType.SELLER.name())
                .orElseThrow(() -> new RuntimeException("SELLER role not found"));

        // Create sample BUYER user
        if (userRepository.findByUsername("buyer1").isEmpty()) {
            User buyerUser = new User();
            buyerUser.setUsername("buyer1");
            buyerUser.setPassword(passwordEncoder.encode("password123"));
            buyerUser.setEmail("buyer1@onepiece-auction.com");
            buyerUser.setFirstName("Buyer");
            buyerUser.setLastName("One");
            buyerUser.setContact(8888888888L);
            buyerUser.getRoles().add(buyerRole);
            userRepository.save(buyerUser);
            log.info("✓ Created sample BUYER user - Username: buyer1");
        }

        // Create sample SELLER user
        if (userRepository.findByUsername("seller1").isEmpty()) {
            User sellerUser = new User();
            sellerUser.setUsername("seller1");
            sellerUser.setPassword(passwordEncoder.encode("password123"));
            sellerUser.setEmail("seller1@onepiece-auction.com");
            sellerUser.setFirstName("Seller");
            sellerUser.setLastName("One");
            sellerUser.setContact(7777777777L);
            sellerUser.getRoles().add(sellerRole);
            userRepository.save(sellerUser);
            log.info("✓ Created sample SELLER user - Username: seller1");
        }

        log.info("✓ Sample users seeding completed");
    }
}