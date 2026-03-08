package com.dormex.config;

import com.dormex.entity.*;
import com.dormex.entity.enums.*;
import com.dormex.repository.*;
import com.github.javafaker.Faker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("demo")
@Order(2)
public class DemoDataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final BlockRepository blockRepository;
    private final RoomRepository roomRepository;
    private final ComplaintRepository complaintRepository;
    private final MessMenuRepository menuRepository;
    private final PasswordEncoder passwordEncoder;

    private final Faker faker = new Faker(new Locale("en-IND"));
    private final Random random = new Random(42); // Fixed seed for reproducible data

    @Override
    @Transactional
    public void run(String... args) {
        // Check if demo data was already loaded
        if (userRepository.findByEmail("demo.loaded@college.edu").isPresent()) {
            log.info("Demo data already exists, skipping...");
            return;
        }

        log.info("Generating demo dataset with JavaFaker...");

        List<Block> blocks = createBlocks();
        List<Room> rooms = createRooms(blocks);
        List<Student> students = createStudents(rooms, 300);
        createComplaints(students);

        // Only insert mess menu if table is empty (unique constraint on day+meal)
        if (menuRepository.count() == 0) {
            createMessMenu();
        } else {
            log.info("Mess menu data already exists, skipping...");
        }

        // Marker to prevent re-running demo data
        userRepository.save(User.builder()
                .name("Demo Loaded Marker")
                .email("demo.loaded@college.edu")
                .password(passwordEncoder.encode("marker"))
                .role(Role.STUDENT)
                .authProvider(AuthProvider.LOCAL)
                .enabled(false)
                .build());

        log.info("Demo dataset created successfully!");
        log.info("================================================");
        log.info("  Demo Credentials:");
        log.info("  All students: <email> / password123");
        log.info("================================================");
    }

    private List<Block> createBlocks() {
        String[][] blockData = {
                {"Block A - Boys", "Boys hostel with standard rooms", "4"},
                {"Block B - Boys", "Boys hostel with premium rooms", "3"},
                {"Block C - Girls", "Girls hostel with standard rooms", "4"},
                {"Block D - Girls", "Girls hostel with premium rooms", "3"}
        };

        List<Block> blocks = new ArrayList<>();
        for (String[] b : blockData) {
            // Skip if block already exists (unique name constraint)
            Block existing = blockRepository.findAll().stream()
                    .filter(block -> block.getName().equals(b[0]))
                    .findFirst().orElse(null);
            if (existing != null) {
                blocks.add(existing);
            } else {
                blocks.add(blockRepository.save(Block.builder()
                        .name(b[0])
                        .description(b[1])
                        .totalFloors(Integer.parseInt(b[2]))
                        .active(true)
                        .build()));
            }
        }
        log.info("Created {} blocks", blocks.size());
        return blocks;
    }

    private List<Room> createRooms(List<Block> blocks) {
        // If rooms already exist, use them instead of creating duplicates
        List<Room> existingRooms = roomRepository.findAll();
        if (!existingRooms.isEmpty()) {
            log.info("Using {} existing rooms", existingRooms.size());
            return existingRooms;
        }

        String[] roomTypes = {"Single", "Double", "Triple"};
        int[] capacities = {1, 2, 3};
        String[][] amenitySets = {
                {"Fan", "Desk", "Cupboard"},
                {"Fan", "Desk", "Cupboard", "Attached Bathroom"},
                {"AC", "Desk", "Cupboard", "WiFi", "Attached Bathroom"}
        };

        List<Room> rooms = new ArrayList<>();

        for (Block block : blocks) {
            // Extract block letter (e.g., 'A' from "Block A - Boys")
            char blockLetter = block.getName().charAt(6);

            for (int floor = 1; floor <= block.getTotalFloors(); floor++) {
                int roomsPerFloor = 20 + random.nextInt(10); // 20-29 rooms per floor

                for (int r = 1; r <= roomsPerFloor; r++) {
                    int typeIdx = random.nextInt(roomTypes.length);
                    int capacity = capacities[typeIdx];
                    int occupancy = random.nextInt(capacity + 1);

                    RoomStatus status;
                    if (occupancy == 0) status = RoomStatus.AVAILABLE;
                    else if (occupancy >= capacity) status = RoomStatus.FULL;
                    else status = RoomStatus.OCCUPIED;

                    String roomNumber = "" + blockLetter + floor + String.format("%02d", r);

                    rooms.add(Room.builder()
                            .block(block)
                            .roomNumber(roomNumber)
                            .floor(floor)
                            .capacity(capacity)
                            .currentOccupancy(occupancy)
                            .status(status)
                            .roomType(roomTypes[typeIdx])
                            .amenities(String.join(", ", amenitySets[typeIdx]))
                            .build());
                }
            }
        }

        roomRepository.saveAll(rooms);
        log.info("Created {} rooms", rooms.size());
        return rooms;
    }

    private List<Student> createStudents(List<Room> rooms, int count) {
        String[] departments = {
                "Computer Science", "Electronics", "Mechanical",
                "Civil Engineering", "Information Technology", "Electrical Engineering"
        };
        String[] years = {"1st", "2nd", "3rd", "4th"};
        String[] deptCodes = {"CS", "EC", "ME", "CE", "IT", "EE"};

        List<Student> students = new ArrayList<>();
        List<User> users = new ArrayList<>();
        Set<String> usedEmails = new HashSet<>();
        Set<String> usedRolls = new HashSet<>();

        for (int i = 0; i < count; i++) {
            String firstName = faker.name().firstName();
            String lastName = faker.name().lastName();
            String name = firstName + " " + lastName;

            // Generate unique email
            String email = (firstName.toLowerCase() + "." + lastName.toLowerCase() + "@college.edu")
                    .replaceAll("[^a-z.@]", "");
            while (usedEmails.contains(email)) {
                email = firstName.toLowerCase() + random.nextInt(999) + "@college.edu";
            }
            usedEmails.add(email);

            int deptIdx = random.nextInt(departments.length);
            int yearIdx = random.nextInt(years.length);
            int admissionYear = 2026 - (yearIdx + 1);

            // Generate unique roll number
            String roll = deptCodes[deptIdx] + admissionYear + String.format("%03d", i + 1);
            while (usedRolls.contains(roll)) {
                roll = deptCodes[deptIdx] + admissionYear + String.format("%03d", random.nextInt(9999));
            }
            usedRolls.add(roll);

            LocalDate dob = faker.date()
                    .birthday(18, 23)
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            // Generate plain 10-digit phone numbers (matching entity's regex ^[0-9]{10,15}$)
            String phone = generatePhoneNumber();
            String guardianPhone = generatePhoneNumber();

            Room room = rooms.get(random.nextInt(rooms.size()));

            User user = User.builder()
                    .name(name)
                    .email(email)
                    .password(passwordEncoder.encode("password123"))
                    .role(Role.STUDENT)
                    .authProvider(AuthProvider.LOCAL)
                    .enabled(true)
                    .build();
            users.add(user);

            Student student = Student.builder()
                    .user(user)
                    .rollNumber(roll)
                    .phone(phone)
                    .department(departments[deptIdx])
                    .year(years[yearIdx])
                    .address(faker.address().fullAddress())
                    .guardianName(faker.name().fullName())
                    .guardianPhone(guardianPhone)
                    .dateOfBirth(dob)
                    .joiningDate(LocalDate.of(admissionYear, 8, 1))
                    .roomId(room.getId())
                    .status(StudentStatus.ACTIVE)
                    .build();
            students.add(student);
        }

        userRepository.saveAll(users);
        studentRepository.saveAll(students);
        log.info("Created {} students", students.size());
        return students;
    }

    private void createComplaints(List<Student> students) {
        List<Complaint> complaints = new ArrayList<>();
        ComplaintCategory[] categories = ComplaintCategory.values();

        // Weighted status distribution: more OPEN/IN_PROGRESS for demo realism
        ComplaintStatus[] weightedStatuses = {
                ComplaintStatus.OPEN, ComplaintStatus.OPEN, ComplaintStatus.OPEN,
                ComplaintStatus.IN_PROGRESS, ComplaintStatus.IN_PROGRESS,
                ComplaintStatus.RESOLVED, ComplaintStatus.CLOSED
        };

        // Realistic complaint templates per category
        Map<ComplaintCategory, String[][]> templates = new LinkedHashMap<>();
        templates.put(ComplaintCategory.ELECTRICAL, new String[][]{
                {"Fan not working", "The ceiling fan has stopped rotating and makes a grinding noise."},
                {"Tubelight flickering", "The tubelight in the room keeps flickering throughout the night."},
                {"Power socket sparking", "The power socket near the study table is sparking when plugging in devices."},
                {"AC not cooling", "The air conditioner is running but not cooling the room at all."},
                {"Switch board damaged", "The main switch board in the room has a cracked cover and exposed wires."}
        });
        templates.put(ComplaintCategory.PLUMBING, new String[][]{
                {"Water leakage in bathroom", "Continuous water dripping from the bathroom tap causing water wastage."},
                {"Blocked drain", "The bathroom drain is completely blocked. Water is not draining at all."},
                {"No hot water", "The geyser is not working. No hot water available for the past 3 days."},
                {"Toilet flush broken", "The toilet flush mechanism is broken and water keeps running continuously."},
                {"Water pressure too low", "Very low water pressure on the 3rd floor, especially during morning hours."}
        });
        templates.put(ComplaintCategory.MAINTENANCE, new String[][]{
                {"Broken door lock", "The main door lock is jammed and cannot be locked properly."},
                {"Window glass cracked", "The window glass has a large crack. Safety concern during rains."},
                {"Cupboard shelf broken", "The middle shelf of the cupboard has broken off and cannot hold anything."},
                {"Ceiling paint peeling", "Large patches of paint are peeling off from the ceiling."},
                {"Bed frame wobbly", "The bed frame is very wobbly and creaks loudly at night."}
        });
        templates.put(ComplaintCategory.CLEANING, new String[][]{
                {"Room not cleaned", "Housekeeping has not cleaned the room for the past 4 days."},
                {"Washroom hygiene issue", "Common washroom on the floor is extremely dirty and smells bad."},
                {"Dustbin not emptied", "The dustbin in the corridor has not been emptied for a week."},
                {"Cobwebs everywhere", "There are cobwebs all over the room corners and near the windows."},
                {"Stains on mattress", "The mattress has large stains and needs to be replaced."}
        });
        templates.put(ComplaintCategory.FOOD, new String[][]{
                {"Poor food quality", "The food quality in dinner has been very poor. Rice is undercooked and dal is watery."},
                {"Insect found in food", "Found an insect in the dal served during lunch today."},
                {"Menu not followed", "The mess is not following the displayed weekly menu at all."},
                {"Not enough variety", "The same menu items are being repeated every alternate day."},
                {"Late meal service", "Dinner service starts 30 minutes late almost every day."}
        });
        templates.put(ComplaintCategory.NOISE, new String[][]{
                {"Loud music at night", "Students playing loud music past midnight regularly."},
                {"Construction noise", "Ongoing construction near the block is extremely noisy during study hours."},
                {"Corridor shouting", "Groups of students shouting and running in the corridor after 11 PM."}
        });
        templates.put(ComplaintCategory.SECURITY, new String[][]{
                {"CCTV not working", "The CCTV camera on the 2nd floor corridor is not operational."},
                {"Unauthorized entry", "Unknown persons seen entering the hostel without valid ID."},
                {"Main gate unlocked", "The main gate was found unlocked after midnight."},
                {"Missing belongings", "Belongings went missing from the common room. Need CCTV footage review."}
        });
        templates.put(ComplaintCategory.ROOMMATE, new String[][]{
                {"Roommate conflict", "Having constant disagreements with roommate about cleanliness and noise."},
                {"Room change request", "Requesting room change due to incompatible living habits with current roommate."},
                {"Personal space issues", "Roommate keeps using my belongings without permission."}
        });
        templates.put(ComplaintCategory.OTHER, new String[][]{
                {"WiFi not working", "The WiFi connectivity in Block A 3rd floor has been down for 2 days."},
                {"Parking issue", "Two-wheeler parking area is overcrowded, need more space allocated."},
                {"Laundry machine broken", "The washing machine on the ground floor has been out of order for a week."}
        });

        String[] adminRemarks = {
                "Maintenance team has been notified. Will be fixed within 24 hours.",
                "Vendor contacted for repair. Estimated completion by tomorrow.",
                "Assigned to housekeeping supervisor for immediate action.",
                "Issue has been fixed. Please confirm and close the complaint.",
                "Taking action. Thank you for reporting this issue.",
                "Inspected the issue. Parts ordered, will be fixed in 2-3 days.",
                "Warning issued to concerned students. Issue should be resolved now.",
                "Escalated to warden for further action."
        };

        for (Student student : students) {
            int complaintsPerStudent = random.nextInt(3); // 0, 1, or 2 complaints each

            for (int i = 0; i < complaintsPerStudent; i++) {
                ComplaintCategory category = categories[random.nextInt(categories.length)];
                ComplaintStatus status = weightedStatuses[random.nextInt(weightedStatuses.length)];

                String[][] categoryTemplates = templates.getOrDefault(category,
                        new String[][]{{"General Issue", "There is an issue that needs attention."}});
                String[] template = categoryTemplates[random.nextInt(categoryTemplates.length)];

                String remarks = (status != ComplaintStatus.OPEN)
                        ? adminRemarks[random.nextInt(adminRemarks.length)]
                        : null;
                LocalDateTime resolvedAt = (status == ComplaintStatus.RESOLVED || status == ComplaintStatus.CLOSED)
                        ? LocalDateTime.now().minusDays(random.nextInt(30))
                        : null;

                complaints.add(Complaint.builder()
                        .student(student)
                        .category(category)
                        .title(template[0])
                        .description(template[1])
                        .status(status)
                        .adminRemarks(remarks)
                        .resolvedAt(resolvedAt)
                        .build());
            }
        }

        complaintRepository.saveAll(complaints);
        log.info("Created {} complaints", complaints.size());
    }

    private void createMessMenu() {
        DayOfWeek[] days = DayOfWeek.values();
        MealType[] meals = MealType.values();

        // Realistic Indian hostel mess menu items
        Map<MealType, String[]> menuItems = new LinkedHashMap<>();
        menuItems.put(MealType.BREAKFAST, new String[]{
                "Idli, Sambar, Coconut Chutney, Tea/Coffee",
                "Poha, Boiled Egg, Bread Butter, Tea/Coffee",
                "Paratha, Curd, Pickle, Tea/Coffee",
                "Upma, Vada, Chutney, Tea/Coffee",
                "Chole Bhature, Lassi, Tea/Coffee",
                "Dosa, Sambar, Coconut Chutney, Tea/Coffee",
                "Puri Sabzi, Halwa, Tea/Coffee"
        });
        menuItems.put(MealType.LUNCH, new String[]{
                "Rice, Dal Tadka, Aloo Gobi, Roti, Salad, Buttermilk",
                "Rice, Chana Dal, Bhindi Fry, Roti, Raita, Papad",
                "Rice, Sambar, Cabbage Poriyal, Roti, Curd, Pickle",
                "Rice, Moong Dal, Baingan Bharta, Roti, Salad, Papad",
                "Jeera Rice, Rajma, Seasonal Veg, Roti, Salad",
                "Veg Biryani, Raita, Salan, Boiled Egg, Salad",
                "Rice, Shahi Paneer, Dal Tadka, Naan, Raita, Jalebi"
        });
        menuItems.put(MealType.SNACKS, new String[]{
                "Samosa, Green Chutney, Tea",
                "Bread Pakora, Ketchup, Tea",
                "Vada Pav, Green Chutney, Tea",
                "Pav Bhaji, Lemon, Tea",
                "Maggi, Tea",
                "Spring Rolls, Sauce, Cold Drink",
                "Cutlet, Ketchup, Tea"
        });
        menuItems.put(MealType.DINNER, new String[]{
                "Rice, Rajma, Mixed Veg, Roti, Pickle, Gulab Jamun",
                "Rice, Paneer Butter Masala, Dal Fry, Roti, Salad",
                "Rice, Chole, Jeera Aloo, Roti, Kheer",
                "Rice, Kadhi Pakora, Aloo Matar, Roti, Pickle",
                "Rice, Malai Kofta, Dal Makhani, Roti, Rasmalai",
                "Rice, Mix Dal, Paneer Tikka Masala, Roti, Pickle",
                "Rice, Egg Curry, Seasonal Veg, Roti, Salad, Ice Cream"
        });

        String[][] specialNotes = {
                {null, null, null, null}, // Monday - no specials
                {null, null, null, "Paneer Special"},
                {"Paratha Day", null, null, null},
                {null, null, "Chef's Special", null},
                {"South Indian", "Biryani Friday!", null, "Premium Dinner"},
                {"Weekend Special", null, null, null},
                {"Sunday Brunch", "Sunday Feast", null, "Sunday Treat"}
        };

        List<MessMenu> menus = new ArrayList<>();

        for (int d = 0; d < days.length; d++) {
            String[] mealTypeItems = null;
            for (int m = 0; m < meals.length; m++) {
                mealTypeItems = menuItems.get(meals[m]);
                String items = mealTypeItems[d % mealTypeItems.length];
                String specialNote = specialNotes[d][m];

                menus.add(MessMenu.builder()
                        .dayOfWeek(days[d])
                        .mealType(meals[m])
                        .items(items)
                        .specialNote(specialNote)
                        .build());
            }
        }

        menuRepository.saveAll(menus);
        log.info("Created {} mess menu entries (7 days x 4 meals)", menus.size());
    }

    /**
     * Generates a plain 10-digit phone number matching the Student entity's
     * regex pattern: ^[0-9]{10,15}$
     */
    private String generatePhoneNumber() {
        StringBuilder sb = new StringBuilder();
        // Start with 7, 8, or 9 (valid Indian mobile prefixes)
        sb.append(7 + random.nextInt(3));
        for (int i = 1; i < 10; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}
