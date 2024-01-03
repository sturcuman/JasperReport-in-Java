package md.turcuman.jasper.jrbean.Model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class Holiday {

    @JsonAlias("NAME")
    private String NAME;

    @JsonAlias("DATA")
    private String DATA;

    @JsonAlias("COUNTRY")
    private String COUNTRY;

}
