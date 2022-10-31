# Spring Boot Exception Handling

Spring framework, hata işleme için mükemmel özelliklere sahiptir. Ancak, exceptionları ele almak ve API istemcisine anlamlı yanıtlar döndürmek için bu özellikleri kullanmak geliştiriciye bırakılmıştır.

@ExceptionHandler, kodun yürütülmesi sırasında ortaya çıkan istisnaları ele almak için bir mekanizma sağlar. Bu açıklama, denetleyici sınıflarının yöntemlerinde kullanılıyorsa, yalnızca bu denetleyici içinde oluşturulan istisnaları işlemek için kullanılır.

@ControllerAdvice,”Spring'de bir ek açıklamadır” ve adından da anlaşılacağı gibi, birden çok denetleyici için "tavsiye" dir. @ControllerAdvice yapıcısı, uygulamanızın yalnızca ilgili bölümünü taramanıza ve yalnızca kurucuda belirtilen ilgili sınıflar tarafından atılan istisnaları işlemenize izin veren bazı özel argümanlarla birlikte gelir. Varsayılan olarak, uygulamanızdaki tüm sınıfları tarayacak ve işleyecektir. Aşağıda, istisnaları işlemek için yalnızca belirli sınıfları kısıtlamak için kullanabileceğimiz bazı türler bulunmaktadır.

Etkilenen denetleyicilerin alt kümesi, @ControllerAdvice üzerinde şu seçiciler kullanılarak tanımlanabilir: 

- ✨ annotations()
- ✨ basePackages()
- ✨ assignableTypes()

## _annotations()_
Belirtilen notlarla açıklamalı olan controller, @ControllerAdvice açıklamalı sınıf tarafından desteklenecek ve bu sınıfların istisnası için uygun olacaktır. Örneğin; @ControllerAdvice(annotations = DoctorController.class)
## _basePackages()_
Taramak istediğimiz paketleri belirterek ve bunlar için istisnaları ele alarak. Örneğin. @ControllerAdvice(basePackages = "org.example.controllers")
## _assignableTypes()_
Bu argüman, belirtilen sınıflardan istisnaları taramayı ve işlemeyi sağlayacaktır. Örneğin. @ControllerAdvice(assignableTypes = { MyController1.class, MyController2.class})
 
> @ControllerAdvice dışında @RestControllerAdvice notasyonu bulunmaktadır. @RestControllerAdvice, hem @ControllerAdvice hem de @ResponseBody'nin birleşimidir. @ControllerAdvice ek açıklamasını RESTful Services'deki istisnaları işlemek için kullanabiliriz ancak @ResponseBody'yi ayrı olarak eklememiz gerekiyor.
 
## Spring Boot Projesinde Uygulanması

Dependecy olarak aşağıdakileri eklememiz gerekmektedir.

```xml
<dependency>
    <groupId>javax.validation</groupId>
    <artifactId>validation-api</artifactId>
    <version>2.0.1.Final</version>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

Uygulamada ilk olarak exceptionları özelleştirmek için aşağıdaki sınıfları oluşturduk.
ErrorResponse.java
```java
@Getter
@Setter
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private final int status;
    private final String message;
    private String stackTrace;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime timestamp = LocalDateTime.now();
    private List<ValidationError> errors;

    @Getter
    @Setter
    @RequiredArgsConstructor
    private static class ValidationError {
        private final String field;
        private final String message;
    }

    public void addValidationError(String field, String message){
        if(Objects.isNull(errors)){
            errors = new ArrayList<>();
        }
        errors.add(new ValidationError(field, message));
    }
}
```
Daha sonra tüm exceptionları yakalamak için hatayakalama sınıfımızı oluşturduk.
GlobalExceptionHandler.java 
```java

@Slf4j(topic = "GLOBAL_EXCEPTION_HANDLER")
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    public static final String TRACE = "trace";

    @Value("${reflectoring.trace:false}")
    private boolean printStackTrace;

    @Override
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY.value(), "Validation error. Check 'errors' field for details.");
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errorResponse.addValidationError(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return ResponseEntity.unprocessableEntity().body(errorResponse);
    }

    @ExceptionHandler(NoSuchElementFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Object> handleNoSuchElementFoundException(NoSuchElementFoundException itemNotFoundException, WebRequest request) {
        log.error("Failed to find the requested element", itemNotFoundException);
        return buildErrorResponse(itemNotFoundException, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> handleAllUncaughtException(Exception exception, WebRequest request) {
        log.error("Unknown error occurred", exception);
        return buildErrorResponse(exception, "Unknown error occurred", HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    private ResponseEntity<Object> buildErrorResponse(Exception exception,
                                                      HttpStatus httpStatus,
                                                      WebRequest request) {
        return buildErrorResponse(exception, exception.getMessage(), httpStatus, request);
    }

    private ResponseEntity<Object> buildErrorResponse(Exception exception,
                                                      String message,
                                                      HttpStatus httpStatus,
                                                      WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(httpStatus.value(), message);
        if (printStackTrace && isTraceOn(request)) {
            errorResponse.setStackTrace(ExceptionUtils.getStackTrace(exception));
        }
        return ResponseEntity.status(httpStatus).body(errorResponse);
    }

    private boolean isTraceOn(WebRequest request) {
        String[] value = request.getParameterValues(TRACE);
        return Objects.nonNull(value)
                && value.length > 0
                && value[0].contentEquals("true");
    }

    @Override
    public ResponseEntity<Object> handleExceptionInternal(
            Exception ex,
            Object body,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {

        return buildErrorResponse(ex, status, request);
    }
```
>Burada dikkat edilmesi gereken husus @ExceptionHandler(NoSuchElementFoundException.class) notasyonudur. Bununla fırlatılacak hata tipine göre yakalamamız sağlanıyor. new NoSuchElementFoundException() olarak fırlatılan exception bu methoda düşüp işlenecektir.

@Valid notasyonu ile de requestleri kontrol edebilmemiz sağlanıyor.
DoctorRequestDto.java
```java
@Getter
@Setter
public class DoctorRequestDto implements Serializable {
    private Long id;
    @NotBlank(message = "Doctor adı boş olamaz")
    private String doctorName;
    @NotBlank(message = "Doctor soyadı boş olamaz")
    private String doctorSurName;
    @NotBlank(message = "Doctor telefon numarası boş olamaz")
    private String phone;
    @NotEmpty( message = "En az bir uzmanlık alanı seçilmeli")
    //@Size(min=1,)
    private List<Long> professionIdList;

}
```
Rest servisinde validation yapılan methodumuza örnek vermek gerekirse;
```java
@RequestMapping(value = "/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
public ResponseEntity<DoctorResponseDto> createDoctor(@RequestBody @Valid DoctorRequestDto doctorRequestDTO) {
    //TODO something
}
```
>@RequestBody @Valid ile birlikte kullanıldığında requestin doğrulaması yapılmaktadır. Hata durumunda şu şekilde response dönülmektedir.

```json
{
    "status": 422,
    "message": "Validation error. Check 'errors' field for details.",
    "timestamp": "30-10-2022 11:48:40",
    "errors": [
        {
            "field": "doctorName",
            "message": "Doctor adı boş olamaz"
        }
    ]
}

```
Bunuda GlobalExceptionHandler.java sınıfımızda handleMethodArgumentNotValid override ederek özelleştirebiliyoruz.

## Özel validasyon notasyonu yazmak
javax.validation.constraints notasyonlarına ek olarak özel doğrulama notasyonları yazılabilir. @IpAddress diye bir doğrulama notasyonu yazalım.
IpAddress.java
```java
@Target({ FIELD })
@Retention(RUNTIME)
@Constraint(validatedBy = IpAddressValidator.class)
@Documented
public @interface IpAddress {

    String message() default "{IpAddress.invalid}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

}
```
IpAddressValidator.java
```java
public class IpAddressValidator implements ConstraintValidator<IpAddress, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        Pattern pattern = Pattern.compile("^([0-9]{1,3})\\.([0-9]{1,3})\\.([0-9]{1,3})\\.([0-9]{1,3})$");
        Matcher matcher = pattern.matcher(value);
        try {
            if (!matcher.matches()) {
                return false;
            } else {
                for (int i = 1; i <= 4; i++) {
                    int octet = Integer.valueOf(matcher.group(i));
                    if (octet > 255) {
                        return false;
                    }
                }
                return true;
            }
        } catch (Exception e) {
            return false;
        }
    }
}

```
Daha sonra bu notasyonu istediğimiz yerde şu şekilde kullanabiliriz:
```java
@IpAddress(message = "Hatalı ip adresi")
private String ip;
```
Bu şekilde request dto sınıfımıza eklediğimiz de hatalı bir ip adresli istekte aşağıdaki şekilde hata dönülecektir.

```json
{
    "status": 422,
    "message": "Validation error. Check 'errors' field for details.",
    "timestamp": "31-10-2022 12:27:49",
    "errors": [
        {
            "field": "ip",
            "message": "Hatalı ip adresi"
        }
    ]
}
```

Uygulamanın tüm kodlarına şuradan ulaşabilirsiniz: [Github](https://github.com/erogluhasan/springboot-exception-handling-demo)

 
