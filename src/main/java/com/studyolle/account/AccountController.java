package com.studyolle.account;

import com.studyolle.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class AccountController {

    //검증 Validator
    private final SignUpFormValidator signUpFormValidator;
    private final AccountService accountService;
    private final AccountRepository accountRepository;

    @InitBinder("signUpForm")
    //Valid를 받을때 Camel-Case로 된 이름의 validator를 알아서 체크
    public void InitBinder(WebDataBinder webDataBinder){
        webDataBinder.addValidators(signUpFormValidator);
    }

    @GetMapping("/sign-up")
    public String signUpForm(Model model){
        model.addAttribute(new SignUpForm());
        return "account/sign-up";
    }

    //이메일 인증메일을 보낸다.
    //토큰을 생성해서 보내기 때문에 서버에서 발급한 요청이 아닌 만들어낸 요청은 거절당한다.
    @PostMapping("/sign-up")
    public String signUpSubmit(@Valid SignUpForm signUpForm, Errors errors){
        if(errors.hasErrors()){
            return "account/sign-up";
        }
        Account account = accountService.processNewAccount(signUpForm);
        accountService.login(account);
        return "redirect:/";
    }

    //이메일 인증메일에서 인증요청을 보내왔을 때 인증 완료가 된다면 유저의 이메일인증여부를 true로 한다.
    @GetMapping("/check-email-token")
    public String checkEmailToken(String email,String token,Model model){
        String view = "account/checked-email";

        Account account = accountRepository.findByEmail(email);
        if(account == null || !account.isValidToken(token)){
            model.addAttribute("error","wrong.email");
            return view;
        }
        accountService.completeSignUp(account);
        model.addAttribute("numberOfUser",accountRepository.count());
        model.addAttribute("nickname",account.getNickname());
        return view;
    }

    @GetMapping("/profile/{nickname}")
    public String viewProfile(@PathVariable String nickname,Model model,@CurrentUser Account currentUser){
        Account byNickname = accountRepository.findByNickname(nickname);
        //To Do : View쪽으로 코드를 수정해야함 현재는 로그로 출력
        if(byNickname == null){
            throw new IllegalArgumentException(nickname + "에 해당하는 사용자가 없습니다.");
        }
        model.addAttribute("account",byNickname);
        model.addAttribute("isOwner",byNickname.equals(currentUser));

        return "account/profile";
    }
}