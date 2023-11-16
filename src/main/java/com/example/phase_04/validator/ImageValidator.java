package com.example.phase_04.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.tika.Tika;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageValidator implements ConstraintValidator<Image, byte[]> {

    @Override
    public void initialize(Image constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(byte[] image, ConstraintValidatorContext constraintValidatorContext) {

        InputStream inputStream = new ByteArrayInputStream(image);
        try {
            Tika tika = new Tika();
            String mimeType = tika.detect(inputStream);
            if(!mimeType.equals("image/jpeg"))
                return false;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return image.length <= 307200;
    }
}

