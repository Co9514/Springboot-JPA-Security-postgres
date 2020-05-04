package com.studyolle.account;

import com.studyolle.domain.Account;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {
    @Autowired private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @MockBean
    JavaMailSender javaMailSender;

    @DisplayName("이메일 인증 확인 - 입력값 오류")
    @Test
    void checkEmailToken_with_wrong_input() throws Exception{
        mockMvc.perform(get("/check-email-token")
                .param("token","asdfasdf")
                .param("email","a40729501@gmail.com"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"))
                .andExpect(view().name("account/checked-email"))
                .andExpect(unauthenticated());
    }

    @DisplayName("이메일 인증 확인 - 입력값 정상")
    @Transactional
    @Test
    void checkEmailToken() throws Exception{
        Account account = Account.builder()
                .email("a93779501@gmail.com")
                .nickname("Co9514")
                .password("1234")
                .build();
        Account newAccount = accountRepository.save(account);
        newAccount.generateToken();
        mockMvc.perform(get("/check-email-token")
                .param("token",newAccount.getEmailCheckTokens())
                .param("email",newAccount.getEmail()))
                .andExpect(status().isOk())
                .andExpect(model().attributeDoesNotExist("error"))
                .andExpect(model().attributeExists("nickname"))
                .andExpect(model().attributeExists("numberOfUser"))
                .andExpect(view().name("account/checked-email"))
                .andExpect(authenticated().withUsername("Co9514"));
    }

    @DisplayName("회원 가입 처리 - 입력값 오류")
    @Test
    void signUpSubmit_with_wrong_input() throws Exception {
        mockMvc.perform(post("/sign-up")
                .param("nickname", "Co9514")
                .param("email", "email..##")
                .param("password", "12345")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"))
                .andExpect(unauthenticated());
    }

    @DisplayName("회원가입 화면 테스트")
    @Test
    void signUpForm() throws Exception {
        mockMvc.perform(get("/sign-up"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"))
                .andExpect(unauthenticated());;
    }

    @DisplayName("회원가입 처리 테스트")
    @Test
    void signUpSubmmitWithCorrectInput() throws Exception{
        mockMvc.perform(post("/sign-up")
                .param("nickname","Co9514")
                .param("email","a93779501@gmail.com")
                .param("password","asdljfk123123!%$")
                //스프링 시큐리티가 제공하는 csrf 보안을 위한 form 토큰 값이 있어야 한다.
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"))
                .andExpect(authenticated().withUsername("Co9514"));;
        Account account = accountRepository.findByEmail("a93779501@gmail.com");
        assertNotNull(account);
        assertNotEquals(account.getPassword(),"asdljfk123123!%$");
        assertNotNull(account.getEmailCheckTokens());
        //메일 send 테스트
        then(javaMailSender).should().send(any(SimpleMailMessage.class));
    }
}