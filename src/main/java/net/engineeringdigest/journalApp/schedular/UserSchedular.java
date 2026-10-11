package net.engineeringdigest.journalApp.schedular;

import net.engineeringdigest.journalApp.Repository.UserRepositoryImpl;
import net.engineeringdigest.journalApp.Service.EmailService;
import net.engineeringdigest.journalApp.cache.AppCache;
import net.engineeringdigest.journalApp.entity.JournalEntry;
import net.engineeringdigest.journalApp.entity.User;
import net.engineeringdigest.journalApp.enums.Sentiment;
import net.engineeringdigest.journalApp.model.SentimentData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Component
public class UserSchedular {


    @Autowired
    private UserRepositoryImpl userRepository;

    @Autowired
    private EmailService emailService;


    @Autowired
    private AppCache appCache;

    @Autowired
    private KafkaTemplate<String, SentimentData> kafkaTemplate;


    @Scheduled(cron = "0 0 */4 * * *")
//@Scheduled(cron = "0 */2 * * * *")
    public void fetchUsersandSendSaEmail(){
        List<User> users = userRepository.getUserForSA();
        System.out.println("Users for SA: " + users.size());
        for(User user:users){
             List<JournalEntry> journalEntries = user.getJournalEntries();
            List<Sentiment> sentiments = journalEntries.stream()
                    .filter(x -> x.getDate()
                            .isAfter(LocalDateTime.now().minus(7, ChronoUnit.DAYS))).map(x->x.getSentiment())
                    .collect(Collectors.toList());
            Map<Sentiment, Integer> sentimentCounts = new HashMap<>();
            for (Sentiment sentiment : sentiments) {
                if (sentiment != null)
                    sentimentCounts.put(sentiment, sentimentCounts.getOrDefault(sentiment, 0) + 1);
            }
            Sentiment mostFrequentSentiment = null;
            int maxCount = 0;
            for (Map.Entry<Sentiment, Integer> entry : sentimentCounts.entrySet()) {
                if (entry.getValue() > maxCount) {
                    maxCount = entry.getValue();
                    mostFrequentSentiment = entry.getKey();
                }
            }
            if (mostFrequentSentiment != null) {
                System.out.println("Sending to Kafka for: " + user.getEmail());
                SentimentData sentimentData = SentimentData.builder()
                        .email(user.getEmail())
                        .sentiment("Sentiment for last 7 days " + mostFrequentSentiment)
                        .build();
                kafkaTemplate.send("weekly_sentiments", sentimentData.getEmail(), sentimentData)
                        .addCallback(
                                r -> System.out.println("Kafka send OK: " + r.getRecordMetadata()),
                                e -> System.out.println("Kafka send FAILED: " + e));
            } else {
                System.out.println("No sentiment for: " + user.getEmail());
            }
        }
    }

    @Scheduled(cron = "0 */10 * * * *")
    public  void clearAppCache(){
        appCache.init();
    }
}
