pipeline {
    agent any

    environment {

        CI = 'true'
        BROWSER = 'remote'

        BASE_UI_URL           = 'http://rbp-assets:80'
        SELENOID_URL          = 'http://selenoid:4444/wd/hub'


        DB_URL                = 'jdbc:mysql://hotel-db:3306/hotel_db'
        KAFKA_BROKERS         = 'kafka:29092'
        WIREMOCK_URL          = 'http://wiremock:8080'

        API_BOOKING_URL       = 'http://rbp-booking:3000'
        API_ROOM_URL          = 'http://rbp-room:3001'
        API_BRANDING_URL      = 'http://rbp-branding:3002'
        API_AUTH_URL          = 'http://rbp-auth:3004'
        API_REPORT_URL        = 'http://rbp-report:3005'
        API_MESSAGE_URL       = 'http://rbp-message:3006'
    }

    tools {
        jdk 'JDK21'
        maven 'M3'
    }

    options {
        timeout(time: 1, unit: 'HOURS')
        ansiColor('xterm')
    }

    stages {
        stage('Checkout Code') {
            steps {
                checkout scm
            }
        }

        stage('Run UI & API Tests') {
            steps {
                echo 'Starting Maven test execution inside Docker network...'
                sh """
                    mvn clean test \
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
            echo 'Generating Allure Report...'
            // Путь к результатам тестов в Maven по умолчанию: target/allure-results
            allure includeProperties: false, jdk: '', results: [[path: 'target/allure-results']]
        }
        success {
            echo 'Pipeline finished successfully! All tests passed.'
        }
        failure {
            echo 'Pipeline failed! Check test failures.'
        }
    }
}
