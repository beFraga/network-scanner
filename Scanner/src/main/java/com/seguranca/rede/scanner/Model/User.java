package com.seguranca.rede.scanner.Model;

import com.seguranca.rede.scanner.Model.PacketInfo.HttpInfos;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@NoArgsConstructor
@Getter
@Setter
public class User implements UserDetails {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    @Column(name = "EMAIL")
    private String email;

    private String senha;
    private boolean enabled = true;

    @Column(name = "ISEMAILVER")
    private boolean isEmailVerified = false;

    @Column(name = "CREATED")
    private Timestamp createdAt;

    @Column(name = "INTERVALO")
    private int interavlo;

    @Column(name = "CONT")
    private int cont;

    @Column(name = "TEMPCONT")
    private int tempoContexto;


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HttpInfos> httpInfosList = new ArrayList<>();

    public void addHttpInfo(HttpInfos httpInfo) {
        httpInfo.setUser(this);
        this.httpInfosList.add(httpInfo);
    }

    // métodos da interface
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return this.senha;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }
}