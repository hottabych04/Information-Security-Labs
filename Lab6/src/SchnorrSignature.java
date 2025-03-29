import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class SchnorrSignature {
    // Параметры схемы
    private final BigInteger p; // Большое простое число
    private final BigInteger q; // Простой делитель p-1
    private final BigInteger g; // Генератор подгруппы порядка q

    // Приватный и публичный ключи
    private final BigInteger privateKey; // x, приватный ключ, x < q
    private final BigInteger publicKey;  // y = g^(-x) mod p (или g^(q-x) mod p)

    private final SecureRandom random;

    /**
     * Конструктор для создания новой пары ключей
     */
    public SchnorrSignature(BigInteger p, BigInteger q, BigInteger g) {
        this.p = p;
        this.q = q;
        this.g = g;
        this.random = new SecureRandom();

        // Генерация приватного ключа
        this.privateKey = new BigInteger(q.bitLength() - 1, random).add(BigInteger.ONE);

        // Вычисление публичного ключа: y = g^(-x) mod p
        // Эквивалентно y = g^(q-x) mod p, так как g^q = 1 mod p
        BigInteger exponent = q.subtract(privateKey).mod(q);
        this.publicKey = g.modPow(exponent, p);
    }

    /**
     * Конструктор с существующими ключами
     */
    public SchnorrSignature(BigInteger p, BigInteger q, BigInteger g, BigInteger privateKey) {
        this.p = p;
        this.q = q;
        this.g = g;
        this.privateKey = privateKey;
        this.random = new SecureRandom();

        // Вычисление публичного ключа
        BigInteger exponent = q.subtract(privateKey).mod(q);
        this.publicKey = g.modPow(exponent, p);
    }

    /**
     * Генерация подписи
     */
    public Signature sign(byte[] message) {
        // Генерация случайного числа k, 0 < k < q
        BigInteger k = new BigInteger(q.bitLength() - 1, random).add(BigInteger.ONE);

        // Вычисление r = g^k mod p
        BigInteger r = g.modPow(k, p);

        // Вычисление e = H(m || r)
        BigInteger e = hash(concatenate(message, r.toByteArray()));

        // Вычисление s = (k + x*e) mod q
        BigInteger s = k.add(privateKey.multiply(e)).mod(q);

        return new Signature(e, s);
    }

    /**
     * Проверка подписи
     */
    public boolean verify(byte[] message, Signature signature, BigInteger publicKey) {
        BigInteger e = signature.e;
        BigInteger s = signature.s;

        // Проверка условий: 0 < e, 0 < s < q
        if (e.compareTo(BigInteger.ZERO) <= 0 || s.compareTo(BigInteger.ZERO) <= 0 || s.compareTo(q) >= 0) {
            return false;
        }

        // Вычисление rv = g^s * y^e mod p
        BigInteger rv = g.modPow(s, p).multiply(publicKey.modPow(e, p)).mod(p);

        // Вычисление ev = H(m || rv)
        BigInteger ev = hash(concatenate(message, rv.toByteArray()));

        // Подпись действительна, если ev = e
        return ev.equals(e);
    }

    /**
     * Класс представляющий подпись
     */
    public static class Signature {
        public final BigInteger e;
        public final BigInteger s;

        public Signature(BigInteger e, BigInteger s) {
            this.e = e;
            this.s = s;
        }

        @Override
        public String toString() {
            return "Signature{e=" + e.toString(16) + ", s=" + s.toString(16) + "}";
        }
    }

    /**
     * Возвращает публичный ключ
     */
    public BigInteger getPublicKey() {
        return publicKey;
    }

    /**
     * Хеш-функция (SHA-256)
     */
    private BigInteger hash(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data);
            // Берем только первую половину хеша для уменьшения размера e
            byte[] truncatedHash = new byte[hash.length / 2];
            System.arraycopy(hash, 0, truncatedHash, 0, truncatedHash.length);
            return new BigInteger(1, truncatedHash).mod(q);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    /**
     * Объединение двух массивов байтов
     */
    private byte[] concatenate(byte[] a, byte[] b) {
        byte[] result = new byte[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

    /**
     * Пример использования
     */
    public static void main(String[] args) {
        // Примеры параметров (для демонстрации используем небольшие значения)
        // В реальном приложении следует использовать более надежные и большие параметры
        BigInteger p = new BigInteger("23", 10);
        BigInteger q = new BigInteger("11", 10);
        BigInteger g = new BigInteger("2", 10);

        // Создание экземпляра с новыми ключами
        SchnorrSignature schnorr = new SchnorrSignature(p, q, g);

        // Сообщение для подписи
        String message = "Hello, Schnorr Signature!";
        byte[] messageBytes = message.getBytes();

        // Вывод ключей
        System.out.println("Приватный ключ: " + schnorr.privateKey);
        System.out.println("Публичный ключ: " + schnorr.getPublicKey());

        // Генерация подписи
        Signature signature = schnorr.sign(messageBytes);
        System.out.println("Сообщение: " + message);
        System.out.println("Подпись: " + signature);

        // Проверка подписи
        boolean isValid = schnorr.verify(messageBytes, signature, schnorr.getPublicKey());
        System.out.println("Подпись действительна: " + isValid);

        // Демонстрация с измененным сообщением
        String tamperedMessage = "Modified message";
        boolean isInvalid = schnorr.verify(tamperedMessage.getBytes(), signature, schnorr.getPublicKey());
        System.out.println("Проверка изменённого сообщения: " + isInvalid);
    }
}