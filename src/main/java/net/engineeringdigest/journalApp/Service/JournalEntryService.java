package net.engineeringdigest.journalApp.Service;

import lombok.extern.slf4j.Slf4j;
import net.engineeringdigest.journalApp.Repository.JournalEntryRepo;
import net.engineeringdigest.journalApp.entity.JournalEntry;
import net.engineeringdigest.journalApp.entity.User;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class JournalEntryService {


    @Autowired
    private JournalEntryRepo journalEntryRepo; //Dependency Injection !!!
    @Autowired
    private UserService userService;



    //  Method for saving entity using pojo

     @Transactional //achieving atomicity here
     // (whole methos treated as one ,if gets failure then rollback to the initial state it was in before)
    public void saveEntry(JournalEntry journalEntry, String userName){
try{
    User user = userService.findByUserName(userName);
    System.out.println("USERNAME RECEIVED: " + userName);
    System.out.println("USER FOUND: " + user);
    journalEntry.setDate(LocalDateTime.now());
    JournalEntry saved = journalEntryRepo.save(journalEntry);
    user.getJournalEntries().add(saved);
    userService.saveUser(user);
} catch (Exception e) {
    System.out.println(e);

    throw new RuntimeException("An error occured while saving entry ", e);
}
    }



    public void saveEntry(JournalEntry journalEntry){
        journalEntryRepo.save(journalEntry);
    }

    public List<JournalEntry> getAll(){
        return journalEntryRepo.findAll();
    }

    public Optional<JournalEntry> findById(ObjectId id){
        return journalEntryRepo.findById(id);

    }

    @Transactional
    public  boolean deleteById(ObjectId id, String userName){
         boolean removed=false;
         try{
        User user=userService.findByUserName(userName);
       removed= user.getJournalEntries().removeIf(x ->x.getId().equals(id));
       if(removed) {
           userService.saveUser(user);
           journalEntryRepo.deleteById(id);
       }
       }catch(Exception e){
            log.error("Error",e);
            throw new RuntimeException("An error has occured,while saving the entry: ",e);
         }
         return removed;
        }
    }
//    public  List<JournalEntry> findByUserName(String userName){
//         return userName;
//
//    }

