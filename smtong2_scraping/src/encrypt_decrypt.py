import os
import sys
import base64
from cryptography.hazmat.primitives.ciphers import Cipher, algorithms, modes
from cryptography.hazmat.primitives.padding import PKCS7
from cryptography.hazmat.backends import default_backend

def encrypt_aes256(plain_text, key):
    """AES256으로 암호화"""
    # Base64로 디코딩된 키 사용
    key_bytes = base64.urlsafe_b64decode(key)

    # 초기화 벡터 (IV)를 생성 (16바이트)
    iv = os.urandom(16)

    # AES256-CBC 모드 암호화
    cipher = Cipher(algorithms.AES(key_bytes), modes.CBC(iv), backend=default_backend())
    encryptor = cipher.encryptor()

    # PKCS7 패딩
    padder = PKCS7(algorithms.AES.block_size).padder()
    padded_data = padder.update(plain_text.encode()) + padder.finalize()

    # 암호화
    encrypted_data = encryptor.update(padded_data) + encryptor.finalize()

    # IV와 암호화 데이터를 Base64 URL-safe로 인코딩 후 반환
    return base64.urlsafe_b64encode(iv + encrypted_data).decode()

def decrypt_aes256(key, encrypted_data):
    """AES256으로 복호화"""
    try:
        # Base64로 인코딩된 키와 암호화된 데이터를 디코딩
        key_bytes = base64.urlsafe_b64decode(key)
        encrypted_data_bytes = base64.urlsafe_b64decode(encrypted_data)

        # IV(첫 16바이트)와 암호화된 텍스트를 분리
        iv = encrypted_data_bytes[:16]
        ciphertext = encrypted_data_bytes[16:]

        # AES256-CBC 모드 복호화
        cipher = Cipher(algorithms.AES(key_bytes), modes.CBC(iv), backend=default_backend())
        decryptor = cipher.decryptor()

        # 복호화하고 패딩 제거
        padded_plaintext = decryptor.update(ciphertext) + decryptor.finalize()
        unpadder = PKCS7(algorithms.AES.block_size).unpadder()
        plaintext = unpadder.update(padded_plaintext) + unpadder.finalize()

        return plaintext.decode()
    except Exception as e:
        raise ValueError(f"복호화 실패: {e}")

def main():
    if len(sys.argv) != 3:
        print("Usage: python encrypt_decrypt.py <encrypt|decrypt> <text>")
        sys.exit(1)

    operation = sys.argv[1]
    text = sys.argv[2]

    # 시스템 환경변수에서 ENCRYPTION_KEY 읽기
    encryption_key = os.getenv('ENCRYPTION_KEY')
    if not encryption_key:
        print("Error: ENCRYPTION_KEY is not set in the environment variables.")
        sys.exit(1)

    try:
        if operation == "encrypt":
            encrypted_text = encrypt_aes256(text, encryption_key)
            print(f"Encrypted Text: {encrypted_text}")
        elif operation == "decrypt":
            decrypted_text = decrypt_aes256(encryption_key, text)
            print(f"Decrypted Text: {decrypted_text}")
        else:
            print("Invalid operation. Use 'encrypt' or 'decrypt'.")
    except Exception as e:
        print(f"Operation failed: {e}")
        sys.exit(1)

if __name__ == "__main__":
    main()
