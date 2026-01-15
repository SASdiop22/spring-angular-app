package edu.miage.springboot.seeders.offers;

import edu.miage.springboot.dao.entities.users.EmployeEntity;
import edu.miage.springboot.dao.entities.offers.JobOfferEntity;
import edu.miage.springboot.dao.entities.offers.JobStatusEnum;
import edu.miage.springboot.dao.repositories.users.EmployeRepository;
import edu.miage.springboot.dao.repositories.offers.JobOfferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

@Component
@Order(4)
public class JobOfferSeeder implements CommandLineRunner {
    @Autowired private JobOfferRepository jobOfferRepository;
    @Autowired private EmployeRepository employeRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (jobOfferRepository.count() > 0) return;

        EmployeEntity dylan = employeRepository.findByUserUsername("dylan.demandeur").get();
        EmployeEntity cathy = employeRepository.findByUserUsername("cathy.employe").orElse(null);

        // === OFFRES INFORMATIQUE ===
        createOffer(
            "Développeur Java Senior",
            "Nous cherchons un développeur Java expérimenté pour rejoindre notre équipe backend. " +
            "Vous serez responsable de la conception et du développement de microservices scalables, " +
            "ainsi que de la maintenance et l'amélioration de notre architecture existante. " +
            "Vous travaillerez avec Spring Boot, PostgreSQL et les technologies cloud. " +
            "Une expérience minimum de 5 ans en développement Java est requise. " +
            "Vous rejoindrez une équipe de 8 développeurs passionnés par la qualité du code.",
            dylan,
            JobStatusEnum.OPEN,
            "IT",
            "Paris",
            75000.0,
            3,
            LocalDate.now().plusDays(30),
            Arrays.asList("Java", "Spring Boot", "PostgreSQL", "Docker"),
            "CDI"
        );

        createOffer(
            "Ingénieur DevOps Cloud",
            "Rejoignez notre équipe DevOps pour gérer et optimiser notre infrastructure cloud AWS. " +
            "Vous serez en charge de l'orchestration des conteneurs avec Kubernetes, " +
            "de l'infrastructure-as-code avec Terraform, et de la mise en place de pipelines CI/CD robustes. " +
            "Vous collaborerez avec les développeurs pour améliorer le processus de déploiement. " +
            "Vous devez avoir au minimum 4 ans d'expérience en DevOps/Cloud.",
            dylan,
            JobStatusEnum.OPEN,
            "IT",
            "Lyon",
            70000.0,
            4,
            LocalDate.now().plusDays(45),
            Arrays.asList("AWS", "Kubernetes", "Terraform", "CI/CD"),
            "CDI"
        );

        createOffer(
            "Lead Frontend React",
            "En tant que Lead Frontend, vous piloterez le développement de nos applications web modernes avec React. " +
            "Vous serez responsable de l'architecture frontend, de la performance et de la qualité du code. " +
            "Vous mentorerez une équipe de 4-5 développeurs frontendend et prendrez part aux décisions techniques. " +
            "Expérience requise: 6+ ans en développement frontend, 3+ ans avec React.",
            dylan,
            JobStatusEnum.OPEN,
            "IT",
            "Toulouse",
            68000.0,
            5,
            LocalDate.now().plusDays(30),
            Arrays.asList("React", "TypeScript", "Angular", "CSS"),
            "CDI"
        );

        createOffer(
            "Analyste Data",
            "Transformez nos données en insights précieux pour guider la stratégie commerciale. " +
            "Vous crerez des modèles prédictifs avec Python et Machine Learning, développerez des dashboards Power BI, " +
            "et optimiserez nos requêtes SQL complexes. Vous travaillerez sur des volumes de données importants " +
            "et présenterez vos découvertes aux décideurs. 3+ ans d'expérience en data analysis requise.",
            dylan,
            JobStatusEnum.OPEN,
            "Data",
            "Bordeaux",
            60000.0,
            3,
            LocalDate.now().plusDays(35),
            Arrays.asList("Python", "SQL", "Power BI", "Machine Learning"),
            "CDI"
        );

        // === OFFRES RESSOURCES HUMAINES ===
        createOffer(
            "Responsable RH",
            "Pilotez la stratégie RH et le développement des talents au sein de notre entreprise. " +
            "Vous gérerez le recrutement, la formation, la gestion de carrière et l'administration du personnel. " +
            "Vous utiliserez notre SIRH pour optimiser les processus et serez responsable de la paie. " +
            "Vous devrez maîtriser le droit social et les normes de travail. 5+ ans d'expérience requise.",
            dylan,
            JobStatusEnum.OPEN,
            "HR",
            "Paris",
            55000.0,
            2,
            LocalDate.now().plusDays(40),
            Arrays.asList("Recrutement", "SIRH", "Gestion de paie", "Droit social"),
            "CDI"
        );

        createOffer(
            "Chargé de Recrutement",
            "Recruter nos talents et accompagner les candidats tout au long du processus de sélection. " +
            "Vous serez en charge du sourcing, de la présélection, de la coordination des entretiens, " +
            "et du suivi des offres. Vous utiliserez LinkedIn et d'autres outils de sourcing modernes. " +
            "Vous devrez développer votre réseau et atteindre les objectifs de recrutement. 2+ ans d'expérience.",
            dylan,
            JobStatusEnum.OPEN,
            "HR",
            "Paris",
            40000.0,
            2,
            LocalDate.now().plusDays(25),
            Arrays.asList("Recrutement", "Sourcing", "Entretiens", "LinkedIn"),
            "CDI"
        );

        // === OFFRES FINANCE ===
        createOffer(
            "Contrôleur de Gestion",
            "Optimisez nos processus financiers et assurez la conformité budgétaire de l'entreprise. " +
            "Vous serez responsable du suivi budgétaire, de l'analyse des écarts et de la production de rapports financiers. " +
            "Vous utiliserez SAP et Excel avancé pour créer des modèles financiers complexes. " +
            "Vous collaborerez avec tous les départements pour contrôler les dépenses. 3+ ans d'expérience requise.",
            dylan,
            JobStatusEnum.OPEN,
            "Finance",
            "Paris",
            52000.0,
            2,
            LocalDate.now().plusDays(30),
            Arrays.asList("Contrôle de gestion", "Excel", "SAP", "Reporting"),
            "CDI"
        );

        createOffer(
            "Expert Comptable",
            "Gérez la comptabilité générale et les déclarations fiscales de notre groupe multi-sociétés. " +
            "Vous serez responsable des écritures comptables, de la clôture mensuelle/annuelle, et des audits. " +
            "Vous devez maîtriser la fiscalité française, les normes IFRS et assurer la conformité légale. " +
            "Diplômé expert-comptable ou titre reconnu. Minimum 5 ans d'expérience.",
            dylan,
            JobStatusEnum.OPEN,
            "Finance",
            "Lyon",
            58000.0,
            1,
            LocalDate.now().plusDays(35),
            Arrays.asList("Comptabilité", "Fiscalité", "Audit", "Conformité"),
            "CDI"
        );

        // === OFFRES MARKETING ===
        createOffer(
            "Responsable Marketing Digital",
            "Pilotez notre stratégie digitale et la croissance de notre présence en ligne. " +
            "Vous gérerez les campagnes SEO/SEM, les stratégies email marketing, et analyserez les performances avec GA4. " +
            "Vous définirez les KPIs, optimiserez les conversions et collaborerez avec l'équipe commerciale. " +
            "Vous devez avoir une bonne compréhension des outils marketing modernes. 4+ ans d'expérience.",
            dylan,
            JobStatusEnum.OPEN,
            "Marketing",
            "Paris",
            48000.0,
            3,
            LocalDate.now().plusDays(30),
            Arrays.asList("SEO", "SEM", "Email Marketing", "Analytics"),
            "CDI"
        );

        createOffer(
            "Community Manager",
            "Animez nos réseaux sociaux et créez du contenu engageant pour notre audience. " +
            "Vous gérerez Facebook, Instagram, LinkedIn et TikTok. Vous créerez du contenu original, " +
            "répondrez aux commentaires, gérerez la communauté et rapporterez les métriques. " +
            "Vous devez être créatif, réactif et comprendre les tendances digitales actuelles. 2+ ans d'expérience.",
            dylan,
            JobStatusEnum.OPEN,
            "Marketing",
            "Toulouse",
            35000.0,
            4,
            LocalDate.now().plusDays(20),
            Arrays.asList("Social Media", "Content Creation", "Community Management", "Copywriting"),
            "CDI"
        );

        // === OFFRES VENTES ===
        createOffer(
            "Account Manager B2B",
            "Développez le portefeuille client et pilotez les ventes de solutions SaaS B2B. " +
            "Vous serez responsable de la prospection, de la négociation et de la fidélisation des clients existants. " +
            "Vous utiliserez Salesforce pour gérer vos pipelines et rapporterez les revenus. " +
            "Vous devez être autonome, orienté résultat et avoir l'anglais courant. 3+ ans en ventes B2B.",
            dylan,
            JobStatusEnum.OPEN,
            "Sales",
            "Paris",
            45000.0,
            2,
            LocalDate.now().plusDays(25),
            Arrays.asList("Ventes B2B", "CRM", "Négociation", "Anglais"),
            "CDI"
        );

        createOffer(
            "Business Development Manager",
            "Prospectez de nouveaux partenaires stratégiques et identifiez de nouveaux marchés. " +
            "Vous serez responsable d'identifier les opportunités de croissance, de négocier les partenariats, " +
            "et de développer des stratégies commerciales innovantes. Vous voyagerez régulièrement pour rencontrer les clients. " +
            "Vous devez avoir l'anglais courant et une expérience en ventes/business dev. 4+ ans d'expérience.",
            dylan,
            JobStatusEnum.OPEN,
            "Sales",
            "Lyon",
            50000.0,
            3,
            LocalDate.now().plusDays(35),
            Arrays.asList("Business Development", "Prospection", "Anglais", "Négociation"),
            "CDI"
        );

        // === OFFRES AVEC STATUT DRAFT (non publiées) ===
        createOffer(
            "Architect Solutions (DRAFT)",
            "Concevez l'architecture technique de nos solutions d'entreprise pour nos clients Fortune 500. " +
            "Vous serez responsable de l'évolutivité, de la performance, de la sécurité et de la maintenance. " +
            "Vous travaillerez avec les microservices, la conteneurisation et les solutions cloud modernes. " +
            "Vous devez justifier d'au moins 8 ans d'expérience en architecture logicielle.",
            dylan,
            JobStatusEnum.DRAFT,
            "IT",
            "Paris",
            85000.0,
            2,
            LocalDate.now().plusDays(60),
            Arrays.asList("Architecture", "Scalabilité", "Microservices", "Cloud"),
            "CDI"
        );

        createOffer(
            "Scrum Master (DRAFT)",
            "Pilotez l'agilité et optimisez les processus de nos équipes de développement. " +
            "Vous serez responsable de la facilitation des cérémonies agile, de la suppression des blocages, " +
            "et de l'amélioration continue des processus. Vous mentorerez les équipes sur les pratiques Agile/Scrum. " +
            "Certification Scrum Master requise. 3+ ans d'expérience en rôle Scrum Master.",
            dylan,
            JobStatusEnum.DRAFT,
            "IT",
            "Bordeaux",
            48000.0,
            3,
            LocalDate.now().plusDays(50),
            Arrays.asList("Agile", "Scrum", "Kanban", "Leadership"),
            "CDI"
        );

        System.out.println("✓ JobOfferSeeder : " + jobOfferRepository.count() + " offres créées");
    }

    private void createOffer(
        String title,
        String description,
        EmployeEntity creator,
        JobStatusEnum status,
        String department,
        String location,
        Double salaryRange,
        Integer remoteDays,
        LocalDate deadline,
        java.util.List<String> skills,
        String contractType
    ) {
        JobOfferEntity offer = new JobOfferEntity();
        offer.setTitle(title);
        offer.setDescription(description);
        offer.setCreator(creator);
        offer.setStatus(status);
        offer.setDepartment(department);
        offer.setLocation(location);
        offer.setSalaryRange(salaryRange);
        offer.setRemoteDays(remoteDays);
        offer.setDeadline(deadline);

        // Ajouter les compétences requises
        if (skills != null) {
            skills.forEach(skill -> offer.getSkills().add(skill));
        }

        // Ajouter des métadonnées supplémentaires
        if (status == JobStatusEnum.OPEN) {
            offer.setPublishedAt(LocalDateTime.now());
        }
        offer.setCreatedAt(LocalDateTime.now());

        jobOfferRepository.save(offer);
    }
}


