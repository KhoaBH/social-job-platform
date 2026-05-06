package vn.edu.uit.socialjob.platform.config;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;
import vn.edu.uit.socialjob.platform.common.enums.ExperienceLevel;
import vn.edu.uit.socialjob.platform.common.enums.JobApplicationStatus;
import vn.edu.uit.socialjob.platform.common.enums.JobPostStatus;
import vn.edu.uit.socialjob.platform.modules.company.entity.Company;
import vn.edu.uit.socialjob.platform.modules.company.repository.CompanyRepository;
import vn.edu.uit.socialjob.platform.modules.jobpost.entity.JobApplication;
import vn.edu.uit.socialjob.platform.modules.jobpost.entity.JobPost;
import vn.edu.uit.socialjob.platform.modules.jobpost.entity.JobSkill;
import vn.edu.uit.socialjob.platform.modules.jobpost.repository.JobApplicationRepository;
import vn.edu.uit.socialjob.platform.modules.jobpost.repository.JobPostRepository;
import vn.edu.uit.socialjob.platform.modules.jobpost.repository.JobSkillRepository;
import vn.edu.uit.socialjob.platform.modules.skill.entity.Skill;
import vn.edu.uit.socialjob.platform.modules.skill.repository.SkillRepository;
import vn.edu.uit.socialjob.platform.modules.user.entity.User;
import vn.edu.uit.socialjob.platform.modules.user.repository.UserRepository;

@Slf4j
@Configuration
public class DevDataLoader {

    @Bean
    CommandLineRunner initDevData(
        UserRepository userRepository,
        CompanyRepository companyRepository,
        SkillRepository skillRepository,
        JobPostRepository jobPostRepository,
        JobSkillRepository jobSkillRepository,
        JobApplicationRepository jobApplicationRepository
    ) {
        return args -> {
            List<User> users = ensureUsers(userRepository);
            List<Company> companies = ensureCompanies(companyRepository, users);
            List<Skill> skills = ensureSkills(skillRepository);
            List<JobPost> jobPosts = ensureJobPosts(jobPostRepository, companies, users);

            ensureJobSkills(jobSkillRepository, jobPosts, skills);
            ensureJobApplications(jobApplicationRepository, jobPosts, users);

            log.info("Dev data initialization completed: users={}, companies={}, skills={}, jobPosts={}",
                userRepository.count(), companyRepository.count(), skillRepository.count(), jobPostRepository.count());
            log.info("Use POST /api/auth/dev-login with any seeded email to get JWT token");
        };
    }

    private List<User> ensureUsers(UserRepository userRepository) {
        List<User> users = userRepository.findAll();
        boolean createdAny = false;

        createdAny |= createUserIfMissing(users, userRepository, "alice@test.com", "alice", "Alice Nguyen", "Software Engineer");
        createdAny |= createUserIfMissing(users, userRepository, "bob@test.com", "bob", "Bob Tran", "Product Manager");
        createdAny |= createUserIfMissing(users, userRepository, "charlie@test.com", "charlie", "Charlie Le", "UI/UX Designer");
        createdAny |= createUserIfMissing(users, userRepository, "diana@test.com", "diana", "Diana Pham", "Data Analyst");
        createdAny |= createUserIfMissing(users, userRepository, "eve@test.com", "eve", "Eve Hoang", "Marketing Specialist");

        if (createdAny) {
            log.info("Ensured seeded dev users are present");
            return userRepository.findAll();
        }

        return users;
    }

    private List<Company> ensureCompanies(CompanyRepository companyRepository, List<User> users) {
        List<Company> companies = companyRepository.findAll();
        User ownerA = findUserByEmail(users, "alice@test.com");
        User ownerB = findUserByEmail(users, "bob@test.com");

        boolean createdAny = false;
        createdAny |= createCompanyIfMissing(companies, companyRepository, ownerA, "Alpha Tech", "https://placehold.co/120x120", "https://alpha-tech.example", true,
            "Product engineering company focused on SaaS platforms.");
        createdAny |= createCompanyIfMissing(companies, companyRepository, ownerB, "Bright Commerce", "https://placehold.co/120x120", "https://bright-commerce.example", false,
            "E-commerce team building marketing and operations tools.");

        if (createdAny) {
            log.info("Ensured seeded dev companies are present");
            return companyRepository.findAll();
        }

        return companies;
    }

    private List<Skill> ensureSkills(SkillRepository skillRepository) {
        if (skillRepository.count() > 0) {
            return skillRepository.findAll();
        }

        List<Skill> skills = List.of(
            createSkill("Java"),
            createSkill("Spring Boot"),
            createSkill("React"),
            createSkill("TypeScript"),
            createSkill("SQL"),
            createSkill("UI Design")
        );

        skillRepository.saveAll(skills);
        log.info("Created {} test skills for development", skills.size());
        return skillRepository.findAll();
    }

    private List<JobPost> ensureJobPosts(JobPostRepository jobPostRepository, List<Company> companies, List<User> users) {
        List<JobPost> jobPosts = jobPostRepository.findAll();
        Company alphaTech = findCompanyByName(companies, "Alpha Tech");
        Company brightCommerce = findCompanyByName(companies, "Bright Commerce");
        User alice = findUserByEmail(users, "alice@test.com");
        User bob = findUserByEmail(users, "bob@test.com");

        boolean createdAny = false;
        createdAny |= createJobPostIfMissing(jobPosts, jobPostRepository,
            createJobPost(alphaTech, alice, "Backend Java Engineer", "Build and maintain core APIs for the platform.",
                ExperienceLevel.MIDDLE, 18000000, 32000000, "Ho Chi Minh City", JobPostStatus.OPEN));
        createdAny |= createJobPostIfMissing(jobPosts, jobPostRepository,
            createJobPost(brightCommerce, bob, "Frontend Engineer", "Create intuitive user experiences for the job dashboard.",
                ExperienceLevel.JUNIOR, 15000000, 28000000, "Remote / Ho Chi Minh City", JobPostStatus.OPEN));

        if (createdAny) {
            log.info("Ensured seeded dev job posts are present");
            return jobPostRepository.findAll();
        }

        return jobPosts;
    }

    private void ensureJobSkills(JobSkillRepository jobSkillRepository, List<JobPost> jobPosts, List<Skill> skills) {
        if (jobSkillRepository.count() > 0) {
            return;
        }

        JobPost backendJob = findJobPostByTitle(jobPosts, "Backend Java Engineer");
        JobPost frontendJob = findJobPostByTitle(jobPosts, "Frontend Engineer");

        Skill javaSkill = findSkillByNameOrNull(skills, "Java");
        Skill springBootSkill = findSkillByNameOrNull(skills, "Spring Boot");
        Skill reactSkill = findSkillByNameOrNull(skills, "React");
        Skill typeScriptSkill = findSkillByNameOrNull(skills, "TypeScript");

        if (javaSkill == null || springBootSkill == null || reactSkill == null || typeScriptSkill == null) {
            log.warn("Skipping dev job skill seed because required skills are missing in database");
            return;
        }

        List<JobSkill> jobSkills = List.of(
            createJobSkill(backendJob, javaSkill, true),
            createJobSkill(backendJob, springBootSkill, true),
            createJobSkill(frontendJob, reactSkill, true),
            createJobSkill(frontendJob, typeScriptSkill, true)
        );

        jobSkillRepository.saveAll(jobSkills);
        log.info("Created {} job skill mappings for development", jobSkills.size());
    }

    private void ensureJobApplications(
        JobApplicationRepository jobApplicationRepository,
        List<JobPost> jobPosts,
        List<User> users
    ) {
        if (jobApplicationRepository.count() > 0) {
            return;
        }

        JobPost backendJob = findJobPostByTitle(jobPosts, "Backend Java Engineer");
        JobPost frontendJob = findJobPostByTitle(jobPosts, "Frontend Engineer");
        User charlie = findUserByEmail(users, "charlie@test.com");
        User diana = findUserByEmail(users, "diana@test.com");

        List<JobApplication> applications = List.of(
            createJobApplication(backendJob, charlie, "Interested in backend architecture and API development.", JobApplicationStatus.APPLIED),
            createJobApplication(frontendJob, diana, "Looking for a product-focused frontend role.", JobApplicationStatus.REVIEWING)
        );

        jobApplicationRepository.saveAll(applications);
        log.info("Created {} job applications for development", applications.size());
    }

    private User createUser(String email, String username, String fullName, String headline) {
        User user = new User();
        user.setEmail(email);
        user.setUsername(username);
        user.setFullName(fullName);
        user.setHeadline(headline);
        user.setAvatarUrl("https://ui-avatars.com/api/?name=" + fullName.replace(" ", "+"));
        return user;
    }

    private boolean createUserIfMissing(
        List<User> users,
        UserRepository userRepository,
        String email,
        String username,
        String fullName,
        String headline
    ) {
        boolean exists = users.stream().anyMatch(user -> email.equalsIgnoreCase(user.getEmail()));
        if (exists) {
            return false;
        }

        User created = userRepository.save(createUser(email, username, fullName, headline));
        users.add(created);
        return true;
    }

    private Company createCompany(User owner, String name, String logoUrl, String website, boolean verified, String description) {
        Company company = new Company();
        company.setOwner(owner);
        company.setName(name);
        company.setLogoUrl(logoUrl);
        company.setWebsite(website);
        company.setVerified(verified);
        company.setDescription(description);
        return company;
    }

    private boolean createCompanyIfMissing(
        List<Company> companies,
        CompanyRepository companyRepository,
        User owner,
        String name,
        String logoUrl,
        String website,
        boolean verified,
        String description
    ) {
        boolean exists = companies.stream().anyMatch(company -> name.equalsIgnoreCase(company.getName()));
        if (exists) {
            return false;
        }

        Company created = companyRepository.save(createCompany(owner, name, logoUrl, website, verified, description));
        companies.add(created);
        return true;
    }

    private Skill createSkill(String name) {
        Skill skill = new Skill();
        skill.setName(name);
        skill.setNameNormalized(name.toLowerCase().replaceAll("\\s+", "-"));
        return skill;
    }

    private JobPost createJobPost(
        Company company,
        User postedBy,
        String title,
        String description,
        ExperienceLevel experienceLevel,
        Integer salaryMin,
        Integer salaryMax,
        String location,
        JobPostStatus status
    ) {
        JobPost jobPost = new JobPost();
        jobPost.setCompany(company);
        jobPost.setPostedBy(postedBy);
        jobPost.setTitle(title);
        jobPost.setDescription(description);
        jobPost.setExperienceLevel(experienceLevel);
        jobPost.setSalaryMin(salaryMin);
        jobPost.setSalaryMax(salaryMax);
        jobPost.setLocation(location);
        jobPost.setStatus(status);
        return jobPost;
    }

    private boolean createJobPostIfMissing(
        List<JobPost> jobPosts,
        JobPostRepository jobPostRepository,
        JobPost jobPost
    ) {
        boolean exists = jobPosts.stream().anyMatch(existing -> jobPost.getTitle().equalsIgnoreCase(existing.getTitle()));
        if (exists) {
            return false;
        }

        JobPost created = jobPostRepository.save(jobPost);
        jobPosts.add(created);
        return true;
    }

    private JobSkill createJobSkill(JobPost jobPost, Skill skill, boolean required) {
        JobSkill jobSkill = new JobSkill();
        jobSkill.setJobPost(jobPost);
        jobSkill.setSkill(skill);
        jobSkill.setRequired(required);
        return jobSkill;
    }

    private JobApplication createJobApplication(JobPost jobPost, User applicant, String coverLetter, JobApplicationStatus status) {
        JobApplication jobApplication = new JobApplication();
        jobApplication.setJobPost(jobPost);
        jobApplication.setApplicant(applicant);
        jobApplication.setAppliedAt(LocalDateTime.now());
        jobApplication.setCoverLetter(coverLetter);
        jobApplication.setStatus(status);
        return jobApplication;
    }

    private User findUserByEmail(List<User> users, String email) {
        return users.stream()
            .filter(user -> email.equalsIgnoreCase(user.getEmail()))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Missing seeded user: " + email));
    }

    private Company findCompanyByName(List<Company> companies, String name) {
        return companies.stream()
            .filter(company -> name.equalsIgnoreCase(company.getName()))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Missing seeded company: " + name));
    }

    private Skill findSkillByName(List<Skill> skills, String name) {
        return skills.stream()
            .filter(skill -> name.equalsIgnoreCase(skill.getName()))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Missing seeded skill: " + name));
    }

    private Skill findSkillByNameOrNull(List<Skill> skills, String name) {
        return skills.stream()
            .filter(skill -> name.equalsIgnoreCase(skill.getName()))
            .findFirst()
            .orElse(null);
    }

    private JobPost findJobPostByTitle(List<JobPost> jobPosts, String title) {
        return jobPosts.stream()
            .filter(jobPost -> title.equalsIgnoreCase(jobPost.getTitle()))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Missing seeded job post: " + title));
    }
}
