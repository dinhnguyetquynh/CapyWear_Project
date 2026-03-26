package org.example.clothing_be.logger;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation dùng để đánh dấu các tham số nhạy cảm (password, token...)
 * để tránh việc ghi log ra bên ngoài.
 */
@Target(ElementType.PARAMETER)//Cái nhãn này được phép dán ở đâu?đặt nó trước các tham số của hàm
@Retention(RetentionPolicy.RUNTIME)//cái nhãn này vẫn tồn tại khi ứng dụng đang chạy
public @interface Sensitive {
}
