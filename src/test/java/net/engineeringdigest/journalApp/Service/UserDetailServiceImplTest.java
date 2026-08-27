//package net.engineeringdigest.journalApp.Service;
//
//import net.engineeringdigest.journalApp.Repository.UserRepository;
//import net.engineeringdigest.journalApp.entity.User;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Disabled;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.ArgumentMatchers;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.security.core.userdetails.UserDetails;
//
//import static org.mockito.Mockito.when;
//@Disabled
//@ExtendWith(MockitoExtension.class)
//public class UserDetailServiceImplTest {
//
//    @InjectMocks
//    private UserDetailServiceImpl userDetailService;
//
//    @Mock
//    private UserRepository userRepository;
//
//    @BeforeEach
//    void setup(){
//        MockitoAnnotations.initMocks(this);
//    }
//    @Disabled
//    @Test
//    void loadUserByUsernameTest() {
//
//        User user = new User("ram", "inrinrick");
//
//
//        when(userRepository.findByUserName(ArgumentMatchers.anyString()))
//                .thenReturn(user);
//
//        UserDetails userDetails =
//                userDetailService.loadUserByUsername("ram");
//
//        Assertions.assertNotNull(userDetails);
//        Assertions.assertEquals("ram", userDetails.getUsername());
//    }
//}