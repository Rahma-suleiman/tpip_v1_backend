package com.znz.tpip_backend;

import org.modelmapper.ModelMapper;
// import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    // @Bean
    // public ModelMapper modelMapper() {
    // return new ModelMapper();
    // }
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        // prevents overwriting existing DB fields with null.(wen updating entit and some fields r missing or null)
        mapper.getConfiguration()
                .setSkipNullEnabled(true); // <-- This is the fix(prevents data loss)
        return mapper;
    }

    // @Bean
    // public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
    //     return builder -> {
    //         builder.modules(new JavaTimeModule());
    //         builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    //     };
    // }

    // add its dependenc in pom.xml
    // @Bean
    // public ObjectMapper objectMapper() {
    //     ObjectMapper mapper = new ObjectMapper();
    //     mapper.registerModule(new JavaTimeModule());
    //     mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    //     // Force LocalTime formatting
    //     JavaTimeModule module = new JavaTimeModule();
    //     module.addSerializer(LocalTime.class,
    //             new LocalTimeSerializer(DateTimeFormatter.ofPattern("HH:mm")));
    //     mapper.registerModule(module);

    //     return mapper;
    // }

 
}

