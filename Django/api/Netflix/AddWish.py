# selenium 불러오기
from selenium import webdriver
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.common.by import By
from pyvirtualdisplay import Display
from api.Netflix.Login import n_login
import time


def n_addwish(email, pwd, name, title):
    display = Display(visible=0, size=(1920, 1080))  # PyCharm 테스트시 주석처리
    display.start()  # PyCharm 테스트시 주석처리

    # chrome창(웹드라이버) 열기  (AWS 경로 : "/home/ubuntu/django_server/chromedriver")
    driver = webdriver.Chrome("/home/ubuntu/django_server/chromedriver")  # PyCharm 테스트시  r"D:\2022 Capston\OOSOO\Python\Watcha\chromedriver.exe"

    # 넷플릭스 로그인
    n_login(email, pwd, name, driver)

    # ----------------------------------------------------------------------------------------------------------------------#

    # 찜 목록에 추가
    # 컨텐츠 검색
    url = "https://www.netflix.com/search?q=" + title
    driver.get(url)

    time.sleep(3)
    driver.implicitly_wait(5)
    WebDriverWait(driver, timeout=10).until(EC.presence_of_element_located((By.CSS_SELECTOR, '#title-card-0-0 > div.ptrack-content > a > div.boxart-size-16x9.boxart-container.boxart-rounded')))

    results = driver.find_elements(By.CSS_SELECTOR, '#title-card-0-0 > div.ptrack-content > a > div.boxart-size-16x9.boxart-container.boxart-rounded')

    for result in results:
        if result.text == title:
            result.click()
            time.sleep(2)
            driver.find_elements(By.CLASS_NAME, "ltr-79elbk")[0].click()
            time.sleep(3)
            driver.implicitly_wait(5)

            driver.quit()
            display.stop()

            return "success"
        else:
            continue

    driver.quit()
    display.stop()

    return "fail"
