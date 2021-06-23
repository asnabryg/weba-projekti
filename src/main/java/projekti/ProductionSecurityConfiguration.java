package projekti;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class ProductionSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;
    
    @Autowired
    private ConfigurableEnvironment env;

    //WebSecurity sec
    @Override
    public void configure(HttpSecurity http) throws Exception {

        http.formLogin().loginPage("/login").loginProcessingUrl("/perform_login")
                .defaultSuccessUrl("/loginSuccess").failureUrl("/login?error=true");
        http.authorizeRequests()
                .antMatchers("/login").permitAll()
                .antMatchers("/register", "/register/**").permitAll()
                .antMatchers("/css", "/css/**").permitAll()
                .antMatchers("/profile", "/profile/**/image/**").authenticated()
                .antMatchers("/profile", "/profile/**").permitAll()
                .antMatchers("/clear").permitAll()
                .antMatchers("/profileImage", "/profileImage/**").permitAll()
                .antMatchers("/images", "/images/**").permitAll()
                .anyRequest().authenticated().and()
                .formLogin().permitAll().and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"));
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
