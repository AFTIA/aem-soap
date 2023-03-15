package me.guillaumecle.aem.soap.core.annotations;

public class InvalidServiceImpl implements ValidService{
    @Override
    public String getMessage() {
        return "Hello";
    }
}
