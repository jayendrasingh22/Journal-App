package net.engineeringdigest.journalApp.Service;

import lombok.extern.slf4j.Slf4j;
import net.engineeringdigest.journalApp.Repository.UserRepository;
import net.engineeringdigest.journalApp.entity.User;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class UserService {
    @Autowired
    private UserRepository userRepository;


    public static final PasswordEncoder passwordEncoder=new BCryptPasswordEncoder();


    //private static final Logger logger= LoggerFactory.getLogger(UserService.class);


    //  Method for saving entity using pojo
    public boolean saveNewUser(User user) {
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRoles(Arrays.asList("USER"));
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            log.info("slf4j jsjsjsjj");
            log.warn("slf4j jsjsjsjj");
            log.debug("slf4j jsjsjsjj");
            log.trace("slf4j jsjsjsjj");
            log.error("Error occured!",user.getUserName(),e);
            return false;

        }
    }

    public void saveAdmin(User user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Arrays.asList("USER","ADMIN"));
        userRepository.save(user);
    }



    public void saveUser (User user){

        userRepository.save(user);
    }


    //getting all the users
    public List<User> getAll(){
        return userRepository.findAll();
    }
  //finding user
    public Optional<User> findById(ObjectId id){
        return userRepository.findById(id);

    }
    //delete user
    public  void deleteById(ObjectId id){
        userRepository.deleteById(id);
    }

//    public User findByUserName(String userName) {
//
//        System.out.println("Searching for: " + userName);
//
//        List<User> users = userRepository.findAll();
//
//        System.out.println("ALL USERS: " + users);
//
//        User user = userRepository.findByUserName(userName);
//
//        System.out.println("FOUND USER: " + user);
//
//        return user;
//    }
    public User findByUserName(String userName){
        return userRepository.findByUserName(userName);
    }
}
