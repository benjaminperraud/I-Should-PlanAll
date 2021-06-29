# Projet de génie logiciel et gestion de projet (INFO-F-307)

Le projet qui est exécutable dans les principaux systèmes d'exploitation du marché, met en œuvre une application simple de gestion de projet . 
L'objectif principal de l'application est de permettre la gestion et le contrôle des projets personnels de manière visuel et simple. Ainsi que la création de nouveaux sous-projets et différentes tâches. 
Le fonctionnement de base est détaillé dans les points suivants. 

# Utilisation

Les bibliothèques nécessaires à la réalisation du projet sont les suivantes
- gson-2.8.6
- jarchivelib-0.7.1-jar-with-dependencies
- junit-4.12
- JUnit5.4 

Ils se trouvent dans le répertoire lib du projet sur GitHub.
La version java requise est Java 1.8 qui intègre JavaFX. 


Certaines librairies nécessitent l'utilisation de Maven. 
Ouvrir la fenêtre Project Structure (Ctrl+Alt+Shift+S ou File-->Project Structure). Dans Project Structure-->Librairies cliquer sur le "+", sélectionner Maven, et installer les trois librairies suivantes : 
- com.google.apis:google-api-services-drive:v3-rev110-1.23.0
- com.google.oauth-client:google-oauth-client-jetty:1.23.0
- com.dropbox.core:dropbox-core-sdk:3.1.5

## Compilation

La compilation de l'application se fait de manière simple dans IntelliJ après avoir correctement configuré les indications établies dans le point de configuration. 

## Démarrage 

Les étapes de base pour une exécution simple du programme sont les suivantes. Après l'exécution du programme, il vous sera demandé votre nom d'utilisateur et votre mot de passe. Par conséquent, dans une première itération, vous devrez vous enregistrer en utilisant le panneau d'enregistrement que vous trouverez dans ce même écran. Une fois inscrit vous devrez vous connecter avec votre username et mot de passe, ensuite vous serez directement redirigé vers le panneau principal où vous pourrez importer, créer ou exporter un projet ainsi que modifier votre profil. Si vous sélectionnez l'option d'importation, il vous sera demandé de donnée le nom du projet que vous voulez importer auquel vous aviez au préalable déja exporter auparavant. Si vous sélectionnez l'option d'exportation, il vous sera demandé d'indiquer le nom avec lequel vous souhaitez exporter le projet et si vous sélectionnez la création d'un nouveau projet, il vous sera demandé les données de base pour créer un projet : nom, description, dates et étiqueté. 

# Configuration :

Java1.8 doit être défini comme la version de compilation et les bibliothèques doivent être ajoutées au répertoire d'exécution dans le panneau de configuration des dépendances du projet. 

# Tests

Trois tests ont été mis en place, ProjectTest;TaskTest et UserTest pour tester la fonctionnalité de l'application. La bibliothèque JUnit5.4 a été utilisée comme support. Les tests exécutables se trouvent dans le dossier test à l'intérieur du répertoire principal du projet. 

# Google Drive

Les projets de l'utilisateur peuvent être exportés et importés vers/depuis son espace de stockage Google Drive, relié à son compte Google. 
La première fois que l'utilisateur utilise cette option, il va être redirigé vers son navigateur pour se connecter et autoriser l'accès de l'application à son compte. Une fois les autorisations acceptées, les fichiers exportés apparaitront dans son Drive.

Note : L'application étant à l'état de test dans Google Cloud, les utilisateurs doivent avoir été ajoutés manuellement auparavant.

Note2 : en cas d'erreur, supprimer le fichier data/tokens/StoredCredentials et réessayer.

# Misc

## Développement

## Screenshot

![1](https://user-images.githubusercontent.com/26284447/110363395-cf5b5c00-8042-11eb-9df4-1cacc7faff31.png)
![2](https://user-images.githubusercontent.com/26284447/110363573-06ca0880-8043-11eb-9582-084092d37ddc.png)
![3](https://user-images.githubusercontent.com/26284447/110363579-092c6280-8043-11eb-9d63-983e4c359dbd.png)
![4](https://user-images.githubusercontent.com/26284447/110363670-1ea18c80-8043-11eb-9afa-c285f509d108.png)


## License
