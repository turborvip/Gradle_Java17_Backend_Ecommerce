package com.turborvip.ecommerce.application.util;

import lombok.Setter;

import java.util.regex.Pattern;

@Setter
public class RegexValidator {
    private Pattern pattern;

    public RegexValidator(String regex){
        pattern = Pattern.compile(regex);
    }

    public boolean validate(final String input) {
        return pattern.matcher(input).matches();
    }
}
