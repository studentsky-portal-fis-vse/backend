# Backend pro studentský portál

![GitHub Repo stars](https://img.shields.io/github/stars/studentsky-portal-fis-vse/backend?style=for-the-badge)
![GitHub contributors](https://img.shields.io/github/contributors/studentsky-portal-fis-vse/backend?style=for-the-badge)
![GitHub issues](https://img.shields.io/github/issues-raw/studentsky-portal-fis-vse/backend?style=for-the-badge)
![GitHub last commit](https://img.shields.io/github/last-commit/studentsky-portal-fis-vse/backend?style=for-the-badge)

## Setup
1. Stáhnout javu 11 (JDK)
2. Naklonovat repozitář
```shell
git clone https://github.com/studentsky-portal-fis-vse/backend
```
3. Ve složce backend spustit terminál
4. Nastavit `development` profil (používá in-memory databázi atd.)
```shell
# Linux 
export SPRING_PROFILES_ACTIVE=development

# Windows?
setx SPRING_PROFILES_ACTIVE development /M
```

5. Zkompilování .jar a spuštění aplikačních testů
```shell
# Linux
./mvnw clean package

# Windows?
.\mvnw.cmd clean package
```

6. Samotné spuštění aplikace
```shell
# Linux
java -jar ./target/backend-0.0.1-SNAPSHOT.jar

# Windows
java -jar target\backend-0.0.1-SNAPSHOT.jar
```