package com.cti.fitnesse;

import fitnesse.junit.FitNesseRunner;
import org.junit.runner.RunWith;
import org.junit.Ignore;

@Ignore
@RunWith(FitNesseRunner.class)
@FitNesseRunner.Suite("FrontPage.ServiceSpecializationTest")
@FitNesseRunner.FitnesseDir(".")
@FitNesseRunner.OutputDir("./target/fitnesse-results")
public class SpecializationServiceFitnesseRunnerTest {
}
