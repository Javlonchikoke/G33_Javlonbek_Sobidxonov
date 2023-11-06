
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.*;

public class WebLogAnalyzer {
    private static final String LOG_FILE_PATH = "access.txt";

    public static void main(String[] args) {
        try {

            LogManager.getLogManager().reset();
            Logger logger = Logger.getLogger(WebLogAnalyzer.class.getName());
            FileHandler fileHandler = new FileHandler("weblog_analyzer.log");
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);

            analyzeLog(logger);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void analyzeLog(Logger logger) throws IOException {
    BufferedReader reader = new BufferedReader(new FileReader(LOG_FILE_PATH));
        String line;
  int totalRequests = 0;
        int total404Errors = 0;
        int ipRequestsCount = 0;

        while ((line = reader.readLine()) != null) {
            totalRequests++;


            LogEntry logEntry = parseLogEntry(line, logger);
            if (logEntry != null) {

                logEntry.logRequestMethodCount(logger);


                if (logEntry.getIpAddress().equals("10.0.0.5")) {
                    ipRequestsCount++;
                }

                if (logEntry.getResponseCode() == 404) {
                    total404Errors++;
                }
            }
        }
        reader.close();

        logger.info("Jami so'rovlar soni: " + totalRequests);
        logger.info("IP manzil uchun so'rovlar soni: " + ipRequestsCount);
        logger.info("HTTP status kodi 404 bo'lgan so'rovlarni soni: " + total404Errors);
    }

    private static LogEntry parseLogEntry(String logLine, Logger logger) {
        String regex = "\\[(.*?)\\] - \\[(.*?)\\] \\[(.*?) (.*?)\\] \"(.*?) (.*?) (.*?)\"";
        LogEntry logEntry = null;

        try {
            if (logLine.matches(regex)) {
                String[] parts = logLine.split(" ");
                String ipAddress = parts[0].substring(1, parts[0].length() - 1);
                String username = parts[2].substring(1, parts[2].length() - 1);
                String timestamp = parts[3] + " " + parts[4].substring(0, parts[4].length() - 1);
                String requestMethod = parts[5].substring(1, parts[5].length());
                String requestPath = parts[6];
                int responseCode = Integer.parseInt(parts[7]);

                logEntry = new LogEntry(ipAddress, username, timestamp, requestMethod, requestPath, responseCode);
            } else {
                logger.warning("Noto'g'ri formatdagi jurnal yozuvi: " + logLine);
            }
        } catch (Exception e) {
            logger.warning("Jurnal yozuvini tahlil qilishda xatolik yuz berdi: " + logLine);
        }

        return logEntry;
    }

    private static class LogEntry {
        private String ipAddress;
        private String username;
        private String timestamp;
        private String requestMethod;
        private String requestPath;
        private int responseCode;

        public LogEntry(String ipAddress, String username, String timestamp, String requestMethod, String requestPath, int responseCode) {
            this.ipAddress = ipAddress;
            this.username = username;
            this.timestamp = timestamp;
            this.requestMethod = requestMethod;
            this.requestPath = requestPath;
            this.responseCode = responseCode;
        }

        public String getIpAddress() {
            return ipAddress;
        }

        public String getUsername() {
            return username;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public String getRequestMethod() {
            return requestMethod;
        }

        public String getRequestPath() {
            return requestPath;
        }

        public int getResponseCode() {
            return responseCode;
        }

        public void logRequestMethodCount(Logger logger) {
            logger.info("So'rov turi: " + requestMethod);
        }
    }
}