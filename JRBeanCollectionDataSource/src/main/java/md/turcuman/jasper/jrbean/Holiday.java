package md.turcuman.jasper.jrbean;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Holiday {

    @JsonAlias("NAME")
    private String name;
    @JsonAlias("DATA")
    @JsonFormat(pattern = "MM/dd/yyyy")
    private Date data;
    @JsonAlias("COUNTRY")
    private String country;


}
