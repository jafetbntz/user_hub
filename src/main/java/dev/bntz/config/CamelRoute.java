package dev.bntz.config;

import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.csv.CsvDataFormat;

import dev.bntz.models.RolesEnum;
import dev.bntz.models.User;
import dev.bntz.rest.json.BooleanDataContainer;

public class CamelRoute extends RouteBuilder {

    /*
     * The regex pattern below matches the following phone patterns
     * 123-456-7890
     * (123) 456-7890
     * 123 456 7890
     * 123.456.7890
     * +91 (123) 456-7890
     */
    private static final String PHONE_PATTERN = "^(\\+\\d{1,2}\\s)?\\(?\\d{3}\\)?[\\s.-]\\d{3}[\\s.-]\\d{4}$";
    
    private static final String EMAIL_PATTERN = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}";
    
    
    private Processor userProcessor = new Processor() {

        @Override
        public void process(Exchange exchange) throws Exception {
            var body =  exchange.getIn().getBody(User.class);

            if (body.getRole() == null) {
                body.setRole(RolesEnum.CLIENT);
            }

            boolean hasAValidEmail = checkMatch(EMAIL_PATTERN, body.getEmail());

            if (!hasAValidEmail) {
                exchange.getMessage().setBody(new BooleanDataContainer<String>(false, "Invalid email"));
                return;
            } 

            var hasAValidPhone = checkMatch(PHONE_PATTERN, body.getWhatsApp());

            if (!hasAValidPhone) {
                exchange.getMessage().setBody(new BooleanDataContainer<String>(false, "Invalid Whastapp"));
                return;
            } 
            exchange.getMessage().setBody(new BooleanDataContainer<User>(true, body));


        }

        /**
         * <p>Compares the text provided with the pattern suplieded to check 
         * if there is a match.
         * @param inputPattern expects a string reresenting the Regex pattern
         * @param text expects a string containing the text to be evaluated
         * @return a boolean indicating if the text matches the provided pattern or not
         */
        private boolean checkMatch(String inputPattern, String text) {
            Pattern pattern = Pattern.compile(inputPattern);
            Matcher matcher = pattern.matcher(text);
            boolean matchFound = matcher.find();
            return matchFound;
        }
        
    };

    @Override
    public void configure() throws Exception {


        from("direct:getAll")
            .bean("userService", "getAll");

        from("direct:getById")
            .bean("userService", "get(*)");

        from("direct:delete")
            .bean("userService", "delete(*)");

        from("direct:create")
            .process(userProcessor).choice()
            .when(simple("${body.success}").isEqualTo(false))
                .endChoice()
            .otherwise()
                .bean("userService", "create(${body.data})")
                .endChoice()
            .end();

        from("direct:update")
            .process(userProcessor).choice()
            .when(simple("${body.success}").isEqualTo(false))
                .endChoice()
            .otherwise()
                .bean("userService", "update(${body.data})")
                .endChoice()
            .end();

        
        from("direct:report")
            .bean("userService", "getReport")
            .marshal(getReportFormat());

    }

    /**
     * <p>This method generates the format required for the CSV report
     * using the name or the properties as headers. The properties are 
     * acquired by reflection, and the order of the deader will define 
     * the order of the columns in the CSV file.</p>
     * @return CsvDataFormat object containing the deader for User.class 
     */
    private CsvDataFormat getReportFormat() {
        Field[] declaredFields = User.class.getDeclaredFields();

        String[] headers =  new String[declaredFields.length];

        var idx = 0;
        for(Field f : declaredFields) {
            headers[idx] = f.getName();
            idx++;
        }

        CsvDataFormat csv = new CsvDataFormat();
        csv.setHeaderDisabled(false);
        csv.setAllowMissingColumnNames(false);
        csv.setHeader(headers);

        return csv;
    }

}
