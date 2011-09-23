package com.rabobank.isd.ecom.mybatis.person;

import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;

/**
 * @author Jamie Craane
 */
public class TotalAgeCalculator implements ResultHandler {
    private int totalAge;

    public void handleResult(final ResultContext resultContext) {
        Person person = (Person) resultContext.getResultObject();
        totalAge += person.getAge();
    }

    public int getTotalAge() {
        return totalAge;
    }
}
