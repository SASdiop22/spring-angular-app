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
            "CDI",
            "TechVision Solutions",
            "TechVision Solutions est une entreprise leader en ingénierie logicielle et transformation digitale. " +
            "Fondée en 2010, nous accompagnons les grandes entreprises dans leurs projets de modernisation technologique. " +
            "Basée à Paris, notre équipe de 150+ experts conçoit des solutions cloud-native innovantes."
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
            "CDI",
            "CloudNative Systems",
            "CloudNative Systems spécialise dans l'infrastructure cloud et DevOps pour les startups tech et PME ambitieuses. " +
            "Depuis 2015, nous aidons nos clients à optimiser leurs déploiements et à réduire leurs coûts cloud. " +
            "Notre expertise couvre AWS, Azure et GCP avec une équipe de 80+ ingénieurs."
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
            "CDI",
            "DigitalFront Agency",
            "DigitalFront Agency est une agence web spécialisée dans la création d'applications web haut de gamme. " +
            "Créée en 2012, nous travaillons avec des clients Fortune 500 et des startups innovantes pour créer des expériences utilisateur exceptionnelles. " +
            "Notre équipe de 60+ développeurs frontend est basée à Toulouse et dans toute la France."
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
            "CDI",
            "DataLake Analytics",
            "DataLake Analytics est le partenaire de référence en intelligence artificielle et big data analytics. " +
            "Depuis 2016, nous aidons les entreprises à valoriser leurs données pour prendre de meilleures décisions. " +
            "Basée à Bordeaux, notre startup de 45+ data scientists et ML engineers travaille sur des projets complexes et stimulants."
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
            "CDI",
            "HR Connect Consulting",
            "HR Connect Consulting est un cabinet de conseil RH spécialisé dans le recrutement et la gestion des talents. " +
            "Depuis 2008, nous accompagnons les PME et ETI dans la construction de leurs politiques RH innovantes. " +
            "Basée à Paris, notre équipe de 30+ consultants travaille sur des missions stratégiques et opérationnelles."
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
            "CDI",
            "TalentHunt Groupe",
            "TalentHunt Groupe est une agence de recrutement spécialisée dans la recherche de talents en ingénierie et digital. " +
            "Créée en 2014, nous plaçons plus de 500 candidats par an dans les meilleures entreprises tech du pays. " +
            "Notre réseau comprend les plus grands groupes français et les startups prometteuses."
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
            "CDI",
            "FinanceFlow Pro",
            "FinanceFlow Pro est un groupe international spécialisé dans la gestion financière et le conseil fiscal. " +
            "Présent dans 25 pays, nous aidons les entreprises à optimiser leur gestion financière et fiscale. " +
            "Basée à Paris avec plus de 200 collaborateurs, notre expertise couvre la consolidation, l'audit et le contrôle de gestion."
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
            "CDI",
            "Expertise Comptable Rhône-Alpes",
            "Expertise Comptable Rhône-Alpes est un cabinet leader en expertise comptable et audit avec plus de 40 ans d'expérience. " +
            "Avec des bureaux à Lyon, Grenoble et Saint-Étienne, nous accompagnons PME et ETI dans leur gestion comptable et fiscale. " +
            "Notre équipe de 50+ experts comptables maîtrise les normes IFRS et les exigences réglementaires."
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
            "CDI",
            "Digital Wave Marketing",
            "Digital Wave Marketing est une agence marketing digitale innovante spécialisée dans le growth marketing et l'acquisition client. " +
            "Depuis 2013, nous aidons les PME et ETI à accélérer leur croissance à travers des stratégies digitales intégrées. " +
            "Notre équipe de 40+ experts couvre SEO, SEM, content marketing et marketing automation."
        );
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
        String contractType,
        String compagnyName,
        String compagnyDescription
    ) {
        JobOfferEntity offer = new JobOfferEntity();
        offer.setTitle(title);
        offer.setDescription(description);
        offer.setCreator(creator);
        offer.setStatus(status);
        offer.setDepartment(department);
        offer.setLocation(location);
        offer.setSalary(salaryRange);
        offer.setRemoteDays(remoteDays);
        offer.setDeadline(deadline);

        // Ajouter les compétences requises
        if (skills != null) {

            skills.forEach(skill -> offer.getSkillsRequired().add(skill));
        }

        // Ajouter des métadonnées supplémentaires
        if (status == JobStatusEnum.OPEN) {
            offer.setPublishedAt(LocalDateTime.now());
        }
        offer.setCreatedAt(LocalDateTime.now());
        offer.setContractType(contractType);
        offer.setCompanyName(compagnyName);
        offer.setCompanyDescription(compagnyDescription);
        jobOfferRepository.save(offer);
    }
}


