# [Katalon Studio] DriverFactoryModifier

## Problem to solve

In a Test Suite "TS2", I want to run Test Case "TC1" 3 times using diffent WebDriver: "Firefox", "Edge Chromium" and "Chrome.

![image](https://kazurayam.github.io/DriverFactoryModifier/images/TS2.png)

## Idea for solution

I thought it is possible to modify `WebUI.openBrowser()` to perform with which browser to run with.
I thought I can implement it using Groovy's Metaprogramming technique.
I tried implementing a Groovy class `com.kazurayam.ks.DriverFactoryModifier`.

However I couldn't succeed.

See [#4](https://github.com/kazurayam/DriverFactoryModifier/issues/4) for the detail.

