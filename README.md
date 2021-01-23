# Généralités
Cette application est un mini-projet d'étude en M1 en mention informatique dans l'établissement **Ecole de Management et d'Innovation Technologique** de Fianarantsoa.  
L'acquisition de ce projet est totalement gratuit et peut être redistribué dans tous le domaines.  
La redistribution et la réutilisation des codes sources et algorithmes sont totalement gratuit.  
Les auteurs  
 - **RAKOTONDRABE As Manjaka Josvah**
 - **RIJANIAINA Elie Fidèle**
  
# Présentation du projet
**Analyse d'image et vision par ordinateur** est un titre générique; mais jusqu'à maintenant, l'application ne permet de traiter que les fondamentaux du traitement d'image simple.  
Le projet est écrit totalement en **Java** mais pourra être retranscrit dans divers langage de programmations.  
Le projet est géré par la gestion de dépendance **Maven**. Il vous faut installer maven qui pourra être trouvé [ici](https://downloads.apache.org/maven/maven-3/3.6.3/binaries/apache-maven-3.6.3-bin.tar.gz) pour lancer les tests et le déployer.  
Pour vérifier que vous avez installé maven, tapez dans une invite de commande.  
```shell
rinelfi@rinelfi-pc: mvn -version
Apache Maven 3.6.3 (cecedd343002696d0abb50b32b541b8a6ba2883f)
Maven home: /home/rinelfi/programs/installed/maven
Java version: 1.8.0_271, vendor: Oracle Corporation, runtime: /home/rinelfi/programs/installed/jdk-8/jre
Default locale: en_US, platform encoding: UTF-8
OS name: "linux", version: "5.4.0-56-generic", arch: "amd64", family: "unix"
```
Vous devriez avoir quelque chose de semblable
## Fonctionnalités attendues (En développement)
Voici les différents fonctionnelités attendues, ceux qui sont terminés et ceux qui sont encore en attente:
```
X : Implémentation et test terminé
W : Test terminé mais pas implémenté
-- : En cours de conception
```

[X] transformation plane:  
- **(X)** Cisaillement
- **(X)** Homothétie
- **(X)** Symétrie verticale
- **(X)** Symétrie horizontale
- **(X)** Symétrie par rapport au centre
- **(--)** Symétrie par rapport à une droite

[X] transformation ponctuelle:
- **(X)** Inversion du niveau de gris
- **(X)** Binarisation à seuillage fixe
- **(X)** Binarisation avec la méthode d'Otsu
- **(X)** Égalisation d'histogramme
- **(W)** Inversion d'histogramme [**Recherche personnel**]
- **(X)** Étalage de la dynamique

[X] traitement locale:
- **(W)** Convolution discrète filtre de Sobel [**Recherche personnel**]
- **(W)** Convolution discrète sans changement de couleurs [**Recherche personnel**]
- **(X)** Convolution discrète 
- **(X)** Filtrage linéaire moyen
- **(X)** Filtrage linéaire gaussien
- **(X)** Lissage conservateur
- **(X)** Lissage médiane
- **(X)** Lissage moyenneur [**Recherche personnel**]

[X] opérations sur les images binaires:
- **(X)** Érosion
- **(X)** Dilatation
- **(X)** Ouverture
- **(X)** Fermeture
- **(X)** Tout ou rien
- **(X)** Epaississement
- **(X)** Top hat à l'ouverture
- **(X)** Top hat à la fermeture

[X] Reconnaissance de forme sur les images binaires:
- **(W)** Contour par érosion [**Recherche personnel**]
- **(W)** Contour par séléction des bords [**Recherche personnel**]
- **(X)** Contour par la tortue de papert
- **(W)** Etiquetage
- **(--)** Squelette

**NB:** les fonctionnalités qui sont encore en phase de test et développement se trouvent dans le fichier compréssé  `ReadyTest.zip` avec son éxécutable

## Fonctionnalités annexes
 - Démarrage de nouveau projet: Séléction de nouveaux images
 - Ouverture d'un projet éxistant
 - Historisation des projets traités et enregistrés
 - Sauvegarde du projet courant dans un type de fichier **portable** `imgproc`
 - Exportation des résultats apparentes aux formats d'images désirés
 - Moyen de revenir dans les traitements précédents ou suivants
 - **Histogramme du traitement courant** avec une vue globale et une vue détaillée qu'on peut naviguer en scrollant le souris
 - Changement de langage de l'application (Pour l'instant  , l'application ne prend en charge que l'anglais et le français)
 - Permet de zoomer l'image au scroll du souris puis naviguer en le déplaçant sur l'écran.
 - Afficher les coordonnées du pointeur du souris par rapport à l'image

# Structures du projet
Dans la vérsion actuelle, voici la structire du projet.
```
analyse_image_et_vision_par_ordinateur
[]-src
|  []-main
|  |  []-java
|  |  |  []-emit.ipcv
|  |  |  |  []-database
|  |  |  |  |  []-dao
|  |  |  |  |  |  []-entites
|  |  |  |  |  |  |  |--DBLanguage.java
|  |  |  |  |  |  |  |--DBProjectHistory.java
|  |  |  |  |  |  |  |--DBSentence.java
|  |  |  |  |  |  |  |--DBTranslation.java
|  |  |  |  |  |  |--DaoFactory.java
|  |  |  |  |  |  |--LanguageDao.java
|  |  |  |  |  |  |--ProjectHistoryDao.java
|  |  |  |  |  |  |--SettingDao.java
|  |  |  |  |  |  |--TranslationDao.java
|  |  |  |  |  []-models
|  |  |  |  |  |  []-sqlite
|  |  |  |  |  |  |  |--LanguageModel.java
|  |  |  |  |  |  |  |--ProjectHistoryModel.java
|  |  |  |  |  |  |  |--SettingModel.java
|  |  |  |  |  |  |  |--TranslationModel.java
|  |  |  |  []-engines
|  |  |  |  |  []-formRecognition
|  |  |  |  |  |  |--PapertTurtle.java
|  |  |  |  |  []-interfaces
|  |  |  |  |  |  []-observerPattern
|  |  |  |  |  |  |  |--Array2DListener.java
|  |  |  |  |  |  |  |--MultipleValueObserver.java
|  |  |  |  |  |  |  |--ObservableClass.java
|  |  |  |  |  |  |  |--ZoomListener.java
|  |  |  |  |  |  |--LinearLocalProcessing.java
|  |  |  |  |  |  |--NonLinearLocalProcessing.java
|  |  |  |  |  |  |--Operations.java
|  |  |  |  |  |  |--PlaneTransformation.java
|  |  |  |  |  |  |--PointTransformation.java
|  |  |  |  |  []-localProcessing
|  |  |  |  |  |  []-linear
|  |  |  |  |  |  |  []-discreteCovolution
|  |  |  |  |  |  |  |  |--CustomFilter1.java
|  |  |  |  |  |  |  |  |--CustomFilter2.java
|  |  |  |  |  |  |  |  |--CustomFilter3.java
|  |  |  |  |  |  |  |  |--SobelFilter.java
|  |  |  |  |  |  |  |--DiscreteCovolution.java
|  |  |  |  |  |  |  |--GaussianFilter.java
|  |  |  |  |  |  |  |--MiddleFilter.java
|  |  |  |  |  |  []-nonLinear
|  |  |  |  |  |  |  |--AverageFilter.java
|  |  |  |  |  |  |  |--ConservativeFilter.java
|  |  |  |  |  |  |  |--MedianFilter.java
|  |  |  |  |  []-operation
|  |  |  |  |  |  |--AllOrNothing.java
|  |  |  |  |  |  |--Dilation.java
|  |  |  |  |  |  |--Erosion.java
|  |  |  |  |  []-PointProcessing
|  |  |  |  |  |  |--DynamicDisplay.java
|  |  |  |  |  |  |--HistogramEqualization.java
|  |  |  |  |  |  |--InvertGrayscale.java
|  |  |  |  |  |  |--ThresholdingBinarization.java
|  |  |  |  |  []-transformation2D
|  |  |  |  |  |  |--Homothety2D.java
|  |  |  |  |  |  |--Rotation2D.java
|  |  |  |  |  |  |--SymmetryO2D.java
|  |  |  |  |  |  |--SymmetryX2D.java
|  |  |  |  |  |  |--SymmetryY2D.java
|  |  |  |  []-guis
|  |  |  |  |  []-customComponents
|  |  |  |  |  |  |--ImageExplorer.java
|  |  |  |  |  |  |--ImageViewer.java
|  |  |  |  |  |  |--ProjectExplorer.java
|  |  |  |  |  |--AllOrNothingDialog.java
|  |  |  |  |  |--CumulativeHistogramDialog.java
|  |  |  |  |  |--DynamicDisplayDialog.java
|  |  |  |  |  |--HistogramDialog.java
|  |  |  |  |  |--HomothetyDialog.java
|  |  |  |  |  |--MainFrame.java
|  |  |  |  |  |--RotationDialog.java
|  |  |  |  |  |--ShearDialog.java
|  |  |  |  |  |--StructuringElementDialog.java
|  |  |  |  |  |--ThresholdingDialog.java
|  |  |  |  []-utils
|  |  |  |  |  []-colors
|  |  |  |  |  |  |--RGB.java
|  |  |  |  |  []-thresholding
|  |  |  |  |  |  |--Otsu.java
|  |  |  |  |  |--ApplicationHistory.java
|  |  |  |  |  |--Const.java
|  |  |  |  |  |--ImageHelper.java
|  |  |  |  |  |--ImageLoader.java
|  |  |  |  |  |--ImageProcessingProjectFile.java
|  |  |  |  |  |--IntArrayHelper2D.java
|  |  |  |  |  |--MatrixElement.java
|  |  |  |  |--Launcher.java
|  |  []-resources
|  |  |  []-emit.ipcv
|  |  |  |  []-database
|  |  |  |  |  |--aivo.sqlite
|  |  |  |  |  |--application.sql
|  |  |  |  []-images
|  |  |  |  |  |--(...)
|  []-test
|--.gitignore
|--nbactions.xml
|--pom.xml
|--README.md
|--ReadyTest.zip
```
(En attente de mise à jour) 
## Mode de déploiement
Comme dit précedement, vous aurez besoin de `Maven` pour compiler le projet.  
Pour cela vous devez respecter les cycles de vie `clean > install > package`; à défaut, certains dépendances seront introuvable du classpath.
Les dépendances seront ensuite copiés dans un dossier `libs/` dont vous ne devez jamais changer le nom; sinon des erreurs peuvent survenir. 
Pour éviter certaines complications, vous devez aussi spécifier la taille mémoire minimal à utiliser. De manière à ce que l'application puisse traiter des images à haute résolutions sans problème.
  
En illustration, voici les étapes de déploiement et d'éxecution:  
`mvn clean install package` va compiler le code source puis éxécuter les tests unitaires, et après créer le binaire(byte codes)
```shell
rinelfi@rinelfi-pc:~/workspace/java/analyse_image_et_vision_par_ordinateur$ mvn clean install package
[INFO] Scanning for projects...
[INFO] 
[INFO] ----------< emit.ipcv:analyse_image_et_vision_par_ordinateur >----------
[INFO] Building analyse_image_et_vision_par_ordinateur 1.1
[INFO] --------------------------------[ jar ]---------------------------------
[INFO] 
[INFO] --- maven-clean-plugin:2.5:clean (default-clean) @ analyse_image_et_vision_par_ordinateur ---
[INFO] Deleting /home/rinelfi/workspace/java/analyse_image_et_vision_par_ordinateur/target
[INFO] 
[INFO] --- maven-resources-plugin:2.6:resources (default-resources) @ analyse_image_et_vision_par_ordinateur ---
[INFO] Using 'UTF-8' encoding to copy filtered resources.
[INFO] Copying 19 resources
[INFO] 
[INFO] --- maven-compiler-plugin:3.1:compile (default-compile) @ analyse_image_et_vision_par_ordinateur ---
[INFO] Changes detected - recompiling the module!
[INFO] Compiling 66 source files to /home/rinelfi/workspace/java/analyse_image_et_vision_par_ordinateur/target/classes
[WARNING] /home/rinelfi/workspace/java/analyse_image_et_vision_par_ordinateur/src/main/java/emit/aivo/guis/CumulativeHistogramDialog.java: Some input files use unchecked or unsafe operations.
[WARNING] /home/rinelfi/workspace/java/analyse_image_et_vision_par_ordinateur/src/main/java/emit/aivo/guis/CumulativeHistogramDialog.java: Recompile with -Xlint:unchecked for details.
[INFO] 
[INFO] --- maven-resources-plugin:2.6:testResources (default-testResources) @ analyse_image_et_vision_par_ordinateur ---
[INFO] Using 'UTF-8' encoding to copy filtered resources.
[INFO] skip non existing resourceDirectory /home/rinelfi/workspace/java/analyse_image_et_vision_par_ordinateur/src/test/resources
[INFO] 
[INFO] --- maven-compiler-plugin:3.1:testCompile (default-testCompile) @ analyse_image_et_vision_par_ordinateur ---
[INFO] Changes detected - recompiling the module!
[INFO] Compiling 7 source files to /home/rinelfi/workspace/java/analyse_image_et_vision_par_ordinateur/target/test-classes
[WARNING] /home/rinelfi/workspace/java/analyse_image_et_vision_par_ordinateur/src/test/java/emit/aivo/utils/TestOtsu.java: /home/rinelfi/workspace/java/analyse_image_et_vision_par_ordinateur/src/test/java/emit/aivo/utils/TestOtsu.java uses or overrides a deprecated API.
[WARNING] /home/rinelfi/workspace/java/analyse_image_et_vision_par_ordinateur/src/test/java/emit/aivo/utils/TestOtsu.java: Recompile with -Xlint:deprecation for details.
[WARNING] /home/rinelfi/workspace/java/analyse_image_et_vision_par_ordinateur/src/test/java/emit/aivo/utils/TestImageHelper.java: /home/rinelfi/workspace/java/analyse_image_et_vision_par_ordinateur/src/test/java/emit/aivo/utils/TestImageHelper.java uses unchecked or unsafe operations.
[WARNING] /home/rinelfi/workspace/java/analyse_image_et_vision_par_ordinateur/src/test/java/emit/aivo/utils/TestImageHelper.java: Recompile with -Xlint:unchecked for details.
[INFO] 
[INFO] --- maven-surefire-plugin:2.12.4:test (default-test) @ analyse_image_et_vision_par_ordinateur ---
[INFO] Surefire report directory: /home/rinelfi/workspace/java/analyse_image_et_vision_par_ordinateur/target/surefire-reports

-------------------------------------------------------
 T E S T S
-------------------------------------------------------
Running emit.ipcv.utils.TestGrayscaleImageHelper


Tests run: 2, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.069 sec
Running emit.ipcv.utils.TestOtsu
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.001 sec
Running emit.ipcv.engines.operations.TestAllOrNothing
[255, 255, 255, 255, 255]
[255, 255, 255, 255, 255]
[255, 255, 255, 255, 255]
[  0, 255, 255, 255, 255]
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.002 sec
Running emit.ipcv.engines.operations.TestErosion
[  0,   0,   0, 255, 255, 255]
[  0,   0,   0,   0,   0, 255]
[  0,   0,   0, 255, 255, 255]
[255, 255, 255, 255, 255, 255]
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.012 sec
Running emit.ipcv.engines.operations.TestDilation
[  0,   0,   0,   0,   0,   0]
[  0,   0,   0,   0,   0,   0]
[  0,   0, 255,   0,   0,   0]
[  0,   0, 255,   0,   0,   0]
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0 sec
Running emit.ipcv.engines.transformation2D.TestShear2D
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.001 sec
Running emit.ipcv.engines.formRecognition.TestPapertTurtle
[255, 255, 255, 255, 255, 255, 255]
[255, 255,   0, 255, 255, 255, 255]
[255,   0,   0,   0, 255,   0, 255]
[255,   0, 255,   0, 255,   0, 255]
[255,   0,   0,   0, 255,   0, 255]
[255, 255,   0, 255, 255,   0, 255]
[255, 255, 255, 255, 255, 255, 255]
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0 sec

Results :

Tests run: 8, Failures: 0, Errors: 0, Skipped: 0

[INFO] 
[INFO] --- maven-jar-plugin:2.3.2:jar (default-jar) @ analyse_image_et_vision_par_ordinateur ---
[INFO] Building jar: /home/rinelfi/workspace/java/analyse_image_et_vision_par_ordinateur/target/analyse_image_et_vision_par_ordinateur-1.1.jar
[INFO] 
[INFO] --- maven-install-plugin:2.4:install (default-install) @ analyse_image_et_vision_par_ordinateur ---
[INFO] Installing /home/rinelfi/workspace/java/analyse_image_et_vision_par_ordinateur/target/analyse_image_et_vision_par_ordinateur-1.1.jar to /home/rinelfi/plugins/maven-local-repositories/emit/aivo/analyse_image_et_vision_par_ordinateur/1.1/analyse_image_et_vision_par_ordinateur-1.1.jar
[INFO] Installing /home/rinelfi/workspace/java/analyse_image_et_vision_par_ordinateur/pom.xml to /home/rinelfi/plugins/maven-local-repositories/emit/aivo/analyse_image_et_vision_par_ordinateur/1.1/analyse_image_et_vision_par_ordinateur-1.1.pom
[INFO] 
[INFO] --- maven-dependency-plugin:2.8:copy-dependencies (default) @ analyse_image_et_vision_par_ordinateur ---
[INFO] Copying VectorGraphics2D-0.13.jar to /home/rinelfi/workspace/java/analyse_image_et_vision_par_ordinateur/target/libs/VectorGraphics2D-0.13.jar
[INFO] Copying fontbox-2.0.21.jar to /home/rinelfi/workspace/java/analyse_image_et_vision_par_ordinateur/target/libs/fontbox-2.0.21.jar
[INFO] Copying bridj-0.7.0.jar to /home/rinelfi/workspace/java/analyse_image_et_vision_par_ordinateur/target/libs/bridj-0.7.0.jar
[INFO] Copying graphics2d-0.28.jar to /home/rinelfi/workspace/java/analyse_image_et_vision_par_ordinateur/target/libs/graphics2d-0.28.jar
[INFO] Copying pdfbox-2.0.21.jar to /home/rinelfi/workspace/java/analyse_image_et_vision_par_ordinateur/target/libs/pdfbox-2.0.21.jar
[INFO] Copying flatlaf-0.45.jar to /home/rinelfi/workspace/java/analyse_image_et_vision_par_ordinateur/target/libs/flatlaf-0.45.jar
[INFO] Copying hamcrest-core-1.3.jar to /home/rinelfi/workspace/java/analyse_image_et_vision_par_ordinateur/target/libs/hamcrest-core-1.3.jar
[INFO] Copying sqlite-jdbc-3.32.3.2.jar to /home/rinelfi/workspace/java/analyse_image_et_vision_par_ordinateur/target/libs/sqlite-jdbc-3.32.3.2.jar
[INFO] Copying junit-4.13.1.jar to /home/rinelfi/workspace/java/analyse_image_et_vision_par_ordinateur/target/libs/junit-4.13.1.jar
[INFO] Copying commons-logging-1.2.jar to /home/rinelfi/workspace/java/analyse_image_et_vision_par_ordinateur/target/libs/commons-logging-1.2.jar
[INFO] Copying flatlaf-intellij-themes-0.45.jar to /home/rinelfi/workspace/java/analyse_image_et_vision_par_ordinateur/target/libs/flatlaf-intellij-themes-0.45.jar
[INFO] Copying webcam-capture-0.3.12.jar to /home/rinelfi/workspace/java/analyse_image_et_vision_par_ordinateur/target/libs/webcam-capture-0.3.12.jar
[INFO] Copying xchart-3.6.6.jar to /home/rinelfi/workspace/java/analyse_image_et_vision_par_ordinateur/target/libs/xchart-3.6.6.jar
[INFO] Copying animated-gif-lib-1.4.jar to /home/rinelfi/workspace/java/analyse_image_et_vision_par_ordinateur/target/libs/animated-gif-lib-1.4.jar
[INFO] Copying slf4j-api-1.7.2.jar to /home/rinelfi/workspace/java/analyse_image_et_vision_par_ordinateur/target/libs/slf4j-api-1.7.2.jar
[INFO] 
[INFO] --- maven-resources-plugin:2.6:resources (default-resources) @ analyse_image_et_vision_par_ordinateur ---
[INFO] Using 'UTF-8' encoding to copy filtered resources.
[INFO] Copying 19 resources
[INFO] 
[INFO] --- maven-compiler-plugin:3.1:compile (default-compile) @ analyse_image_et_vision_par_ordinateur ---
[INFO] Nothing to compile - all classes are up to date
[INFO] 
[INFO] --- maven-resources-plugin:2.6:testResources (default-testResources) @ analyse_image_et_vision_par_ordinateur ---
[INFO] Using 'UTF-8' encoding to copy filtered resources.
[INFO] skip non existing resourceDirectory /home/rinelfi/workspace/java/analyse_image_et_vision_par_ordinateur/src/test/resources
[INFO] 
[INFO] --- maven-compiler-plugin:3.1:testCompile (default-testCompile) @ analyse_image_et_vision_par_ordinateur ---
[INFO] Nothing to compile - all classes are up to date
[INFO] 
[INFO] --- maven-surefire-plugin:2.12.4:test (default-test) @ analyse_image_et_vision_par_ordinateur ---
[INFO] Skipping execution of surefire because it has already been run for this configuration
[INFO] 
[INFO] --- maven-jar-plugin:2.3.2:jar (default-jar) @ analyse_image_et_vision_par_ordinateur ---
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  9.935 s
[INFO] Finished at: 2021-01-15T09:41:30+03:00
[INFO] ------------------------------------------------------------------------
rinelfi@rinelfi-pc:~/workspace/java/analyse_image_et_vision_par_ordinateur$ 
```
Pour l'éxécution:
```shell
rinelfi@rinelfi-pc:~/workspace/java/analyse_image_et_vision_par_ordinateur$ java -jar -Xms1024m -Xmx4096m target/analyse_image_et_vision_par_ordinateur-1.1.jar
[INFO] Checking arguments
[INFO] Enable openGL engine
[INFO] Starting the app
[INFO] Window components initializations
[INFO] Database connection
[INFO] Loading settings
[INFO] Loading application language
```
Ici j'ai initializé la mémoire minimale à allouer à `1 Gb` de mémoire et la mémoire maximale à `4 Gb`, vous êtes ensuite libre de le changer comme vous le voulez.