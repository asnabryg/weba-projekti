package projekti;

import org.eclipse.jetty.http.HttpMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
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
                .defaultSuccessUrl("/loginSuccess").failureUrl("/login?error=true");
        http.authorizeRequests()
                .antMatchers("/login").permitAll()
                .antMatchers("/register","/register/**").permitAll()
                .antMatchers("/h2-console","/h2-console/**").permitAll()
                .antMatchers("/css", "/css/**").permitAll()
                .antMatchers("/profile", "/profile/**/image/**").authenticated()
                .antMatchers("/profile", "/profile/**").permitAll()
                .antMatchers("/clear").permitAll()
                .antMatchers("/profileImage", "/profileImage/**").permitAll()
                .antMatchers("/images", "/images/**").permitAll()
                .anyRequest().authenticated().and()
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
