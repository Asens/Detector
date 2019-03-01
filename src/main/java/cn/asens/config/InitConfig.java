package cn.asens.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;

/**
 * @author Asens
 */

@Component
@Log4j2
public class InitConfig {
    @Value("${absolute-path}")
    private String absolutePath;

    @PostConstruct
    public void init(){
        String path = determinPath();
        log.info(path);
        if(Strings.isNullOrEmpty(path)){
            log.error("can't find detector.yml,set " +
                    "--absolute-path=/your/path/detector.yml," +
                    "or put ./detector.yml or ./config/detector.yml ");
            throw new IllegalStateException("can't find detector.yml,set " +
                    "--absolute-path=/your/path/detector.yml," +
                    "or put ./detector.yml or ./config/detector.yml ");

        }

        Yaml yaml = new Yaml();
        String configJson = "";
        try {
            HashMap map = yaml.loadAs(new FileInputStream(new File(path)),HashMap.class);
            configJson = JSON.toJSONString(map);
        }catch (Exception e){
            log.error("invalidate format of detector.yml,error : {} "+e.getMessage());
            throw new IllegalStateException("invalidate format of detector.yml");
        }

        JSONObject object = JSON.parseObject(configJson);

        JSONObject config = object.getJSONObject("config");
        log.info(configJson);

    }

    private String determinPath() {
        if(!Strings.isNullOrEmpty(absolutePath)){
            File file = new File(absolutePath);
            if(!file.exists()){
                log.error("can't find detector.yml,set " +
                        "--absolute-path=/your/path/detector.yml");
                throw new IllegalStateException("can't find detector.yml,set " +
                        "--absolute-path=/your/path/detector.yml");
            }
            return absolutePath;
        }

        String userDir = System.getProperty("user.dir");

        String path = userDir+File.separator+"config"+File.separator+"detector.yml";

        File file = new File(path);

        if(file.exists()){
            return path;
        }

        path = userDir+File.separator+"detector.yml";
        file = new File(path);

        if(file.exists()){
            return path;
        }

        return null;
    }
}
