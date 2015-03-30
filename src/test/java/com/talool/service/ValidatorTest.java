package com.talool.service;

import org.junit.Assert;
import org.junit.Test;

import com.talool.utils.ValidatorUtils;


public class ValidatorTest {

  @Test
  public void testValidUsername() {

    Assert.assertFalse(ValidatorUtils.isValidUsername("chrisrocks!"));


    Assert.assertTrue(ValidatorUtils.isValidUsername("chris303"));

    Assert.assertTrue(ValidatorUtils.isValidUsername("18812"));

    Assert.assertTrue(ValidatorUtils.isValidUsername("1ab2"));

    Assert.assertTrue(ValidatorUtils.isValidUsername("chris_lintz"));

    Assert.assertTrue(ValidatorUtils.isValidUsername("chris----lintz"));

    Assert.assertTrue(ValidatorUtils.isValidUsername("_chris-Lintz303-_"));

    Assert.assertFalse(ValidatorUtils.isValidUsername("=chris-Lintz303-_"));

    Assert.assertFalse(ValidatorUtils.isValidUsername("+"));

    Assert.assertFalse(ValidatorUtils.isValidUsername("chris?303"));

    Assert.assertFalse(ValidatorUtils.isValidUsername("select * from"));

    Assert.assertFalse(ValidatorUtils.isValidUsername("select * from"));

    Assert.assertFalse(ValidatorUtils.isValidUsername("'chris'"));

    Assert.assertFalse(ValidatorUtils.isValidUsername("?chris\""));

    Assert.assertFalse(ValidatorUtils.isValidUsername("chris!"));



  }
}
