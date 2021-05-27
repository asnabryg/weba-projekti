package projekti;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class DevelopmentSecurityConfiguration extends WebSecurityConfigurerAdapter {
    
    @Autowired
    private UserDetailsService userDetailsService;

    //WebSecurity sec
    @Override
    public void configure(HttpSecurity http) throws Exception {
        
        // MUISTA POISTAA TÄÄ LOPPUPALAUTUKSESSA
        http.csrf().disable();
        http.headers().frameOptions().sameOrigin();
        // Pyyntöjä ei tarkasteta
        //sec.ignoring().antMatchers("/**");
        
        //authenticated()
        http.formLogin().loginPage("/login").loginProcessingUrl("/perform_login")
                .defaultSuccessUrl("/index").failureUrl("/login?error=true");
        http.authorizeRequests()
                .antMatchers("/register","/register/**").permitAll()
                .antMatchers("/h2-console","/h2-console/**").permitAll()
                .anyRequest().permitAll().and()
                .formLogin().permitAll().and()
                .logout().permitAll();
    }
    
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
