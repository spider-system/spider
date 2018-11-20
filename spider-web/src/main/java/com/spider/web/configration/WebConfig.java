package com.spider.web.configration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;

/**
 * @author: wangpeng
 * @date: 2018/11/20 22:06
 */
@Configuration
public class WebConfig {

    @Bean
    @Primary
    public ObjectMapper ObjectMapper(){
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleDateFormat myDateFormat = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        objectMapper.setDateFormat(myDateFormat);
        objectMapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        SimpleModule module = new SimpleModule();
        module.addSerializer(Date.class, new DateSerializer());
        objectMapper.registerModule(module);
        return objectMapper;
    }


    public static class DateSerializer extends JsonSerializer<Date> {
        @Override
        public void serialize(Date value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            jgen.writeString(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date(value.getTime())));
        }
    }

}
