
# SteganographyPasswordManager

SteganographyPasswordManager is a password manager that hides Unicode text inside .png images using Least Significant Bit (LSB) modification for steganographic encoding.

This project was originally developed as a high school assignment, which led to some over-engineered design decisions made to satisfy project requirements. These include (but are not limited to):

    - Treating each pixel as its own object

    - Subclassing BufferedImage via a custom SteganographyImage class

    - Various other unconventional implementations

⚠️ Warning: Do not use this for real password storage.
Passwords are encoded in plain text and this project is not secure.

## Authors

- [@OwenCracknell](https://github.com/OwenCracknell/)


## Usage

please don't
```

