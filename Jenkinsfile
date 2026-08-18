pipeline {
    agent any

    environment {
        CI = 'true'
        BROWSER = 'remote' // Указываем нашей фабрике DriverFactory использовать RemoteWebDriver

        // UI точка входа (Фронтенд для Selenoid браузера внутри Docker сети)
        BASE_UI_URL           = 'http://rbp-assets:80'

        // Инфраструктура внутри Docker сети
        SELENOID_URL          = 'http://selenoid:4444/wd/hub'
        DB_URL                = 'jdbc:mysql://hotel-db:3306/hotel_db'
        KAFKA_BROKERS         = 'kafka:29092'
        WIREMOCK_URL          = 'http://wiremock:8080'

        // API Эндпоинты ВСЕХ микросервисов платформы для RestAssured тестов
        API_BOOKING_URL       = 'http://rbp-booking:3000'
        API_ROOM_URL          = 'http://rbp-room:3001'
        API_BRANDING_URL      = 'http://rbp-branding:3002'
        API_AUTH_URL          = 'http://rbp-auth:3004'
        API_REPORT_URL        = 'http://rbp-report:3005'
        API_MESSAGE_URL       = 'http://rbp-message:3006'
    }

    tools {
        jdk 'JDK21'   // Имя вашей JDK из Global Tool Configuration в Jenkins
        maven 'M3'    // Имя вашего Maven из Global Tool Configuration в Jenkins
    }

    options {
        timeout(time: 1, unit: 'HOURS')
        ansiColor('xterm') // Включает цветное отображение логов Maven в консоли
    }

    stages {
        stage('Checkout Code') {
            steps {
                checkout scm
            }
        }

                stage('Run UI & API Tests') {
                    steps {
                        echo 'Starting Maven test execution strictly using pom.xml path...'
                        sh """
                            mvn clean test -f end-to-end-tests/pom.xml \
                            -Dmaven.test.failure.ignore=true \
                            -Dui.base.url=${BASE_UI_URL} \
                            -Dremote.web.driver.url=${SELENOID_URL} \
                            -Ddb.url=${DB_URL} \
                            -Dkafka.bootstrap.servers=${KAFKA_BROKERS} \
                            -Dwiremock.url=${WIREMOCK_URL} \
                            -Dapi.booking.url=${API_BOOKING_URL} \
                            -Dapi.room.url=${API_ROOM_URL} \
                            -Dapi.branding.url=${API_BRANDING_URL} \
                            -Dapi.auth.url=${API_AUTH_URL} \
                            -Dapi.report.url=${API_REPORT_URL} \
                            -Dapi.message.url=${API_MESSAGE_URL}
                        """
                    }
                }

    }

        post {
            always {
                echo 'Generating Allure Report searching across all submodules...'
                // Маска **/ заставит Jenkins найти папку allure-results, где бы она ни находилась
                allure includeProperties: false, jdk: '', results: [[path: '**/allure-results']]
            }

        success {
            echo '=================================================='
            echo 'Pipeline finished successfully! All tests passed.'
            echo '=================================================='
        }
        failure {
            echo '=================================================='
            echo 'Pipeline failed! Check test failures or logs.'
            echo '=================================================='
        }
    }
}
