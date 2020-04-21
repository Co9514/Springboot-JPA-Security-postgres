package com.studyolle.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
//Getter Setter 설정과 EqualsAndHashCode 는 보통은 id만 두고 여러개 둘경우 무한루프가 일어날 수 있다고 함
//자바 bean에서 동등성 비교를 위해 equals와 hashcode 메소드를 오버라이딩해서 사용하는데,
//@EqualsAndHashCode어노테이션을 사용하면 자동으로 이 메소드를 생성할 수 있다.
//연관관계가 복잡해질때 서로 다른 연관관계를 순환참조하느라 무한루프발생하여 스택오버플로우 발생가능하기에
//id만 주로 넣는다.
@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder @AllArgsConstructor @NoArgsConstructor
public class Account {

    //AUTO INCREMENT 설정 및 PRIVATE KEY 설정
    @Id @GeneratedValue
    private long id;

    //프라이빗으로 묶지 않고 유니크 컬럼으로 지정하면 유니크할 수 있다.
    @Column(unique = true)
    private String nickname;

    @Column(unique = true)
    private String email;

    private String password;

    private boolean emailVerified;

    private String emailCheckTokens;

    private LocalDateTime joinedAt;

    private String bio;

    private String url;

    private String occupation;

    private String location;

    //Lob 어노테이션은 길이 제한이 없다.
    // Basic은 LAZY와 EAGER가 있는데 쿼리를 보낼때 N+1 인지 아닌지의 차이라고 봄 나중에 다시 찾아볼 것
    @Lob @Basic(fetch = FetchType.EAGER)
    private String profileImage;

    private boolean studyCreateByEmail;

    private boolean studyCreateByWeb;

    private boolean studyEnrollmentResultByEmail;

    private boolean studyEnrollmentResultByWeb;

    private boolean studyUpdateByEmail;

    private boolean studyUpdateByWeb;

    public void generateToken() {
        this.emailCheckTokens = UUID.randomUUID().toString();
    }

    public void completeSighUp() {
        this.emailVerified = true;
        this.joinedAt = LocalDateTime.now();
    }

    public boolean isValidToken(String token) {
        return this.emailCheckTokens.equals(token);
    }
}
