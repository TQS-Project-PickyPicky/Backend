package tqs.project.backend.web;


import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import tqs.project.backend.data.collection_point.CollectionPoint;
import tqs.project.backend.data.collection_point.CollectionPointRepository;
import tqs.project.backend.data.parcel.Parcel;
import tqs.project.backend.data.parcel.ParcelRepository;
import tqs.project.backend.data.parcel.ParcelStatus;
import tqs.project.backend.data.partner.Partner;
import tqs.project.backend.data.partner.PartnerRepository;
import tqs.project.backend.data.store.StoreRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestLoginTest {

  @LocalServerPort
  int localPort;

  @Autowired
  private CollectionPointRepository collectionPointRepository;

  @Autowired
  private ParcelRepository parcelRepository;

  @Autowired
  private StoreRepository adminRepository;

  @Autowired
  private PartnerRepository partnerRepository;


  private WebDriver driver;

  @Before
  public void setUp() {
    driver = new FirefoxDriver();
    collectionPointRepository.deleteAll();
    adminRepository.deleteAll();
    partnerRepository.deleteAll();
    parcelRepository.deleteAll();

    CollectionPoint cp1 = new CollectionPoint();
    cp1.setName("Collection Point 1");
    cp1.setType("Collection Point");
    cp1.setCapacity(100);
    cp1.setAddress("Rua do ISEP");
    cp1.setLatitude(41.178);
    cp1.setLongitude(-8.608);
    cp1.setOwnerName("João");
    cp1.setOwnerEmail("joao@ua.pt");
    cp1.setOwnerGender("M");
    cp1.setOwnerPhone(123456789);
    cp1.setOwnerMobilePhone(987654321);
    cp1.setStatus(true);

    CollectionPoint cp2 = new CollectionPoint();
    cp2.setId(2);
    cp2.setName("Collection Point 2");
    cp2.setType("Collection Point");
    cp2.setCapacity(100);
    cp2.setAddress("Rua do Prof António");
    cp2.setLatitude(41.174660);
    cp2.setLongitude(-8.588069);
    cp2.setOwnerName("João");
    cp2.setOwnerEmail("joao@ua.pt");
    cp2.setOwnerGender("M");
    cp2.setOwnerPhone(123456789);
    cp2.setOwnerMobilePhone(987654321);
    cp2.setStatus(false);


    Parcel parcel = new Parcel();
    parcel.setToken(123456);
    parcel.setClientName("João");
    parcel.setClientEmail("joao@ua.pt");
    parcel.setClientPhone(123456789);
    parcel.setClientMobilePhone(987654321);
    parcel.setExpectedArrival(LocalDate.now().plusDays(5));
    parcel.setStatus(ParcelStatus.IN_TRANSIT);
    parcel.setCollectionPoint(cp1);

    Parcel parcel2 = new Parcel();
    parcel2.setToken(123456);
    parcel2.setClientName("Jorge");
    parcel2.setClientEmail("jorge@ua.pt");
    parcel2.setClientPhone(123456789);
    parcel2.setClientMobilePhone(987654321);
    parcel2.setExpectedArrival(LocalDate.now().plusDays(7));
    parcel2.setStatus(ParcelStatus.DELIVERED);
    parcel2.setCollectionPoint(cp1);

    Parcel parcel3 = new Parcel();
    parcel3.setToken(123456);
    parcel3.setClientName("Gabriel");
    parcel3.setClientEmail("gabriel@ua.pt");
    parcel3.setClientPhone(123456789);
    parcel3.setClientMobilePhone(987654321);
    parcel3.setExpectedArrival(LocalDate.now());
    parcel3.setStatus(ParcelStatus.COLLECTED);
    parcel3.setCollectionPoint(cp1);

    Parcel parcel4 = new Parcel();

    List<Parcel> parcels = new ArrayList<>();
    parcels.add(parcel);
    parcels.add(parcel2);
    parcels.add(parcel3);

    cp1.setParcels(parcels);

    Partner partner1 = new Partner();
    partner1.setPassword("pass123");
    partner1.setUsername("username1");
    partner1.setCollectionPoint(cp1);

    Partner partner2 = new Partner();
    partner2.setPassword("pass123");
    partner2.setUsername("username1");
    partner2.setCollectionPoint(cp2);

    cp1.setPartner(partner1);
    cp2.setPartner(partner2);

    cp1 = collectionPointRepository.save(cp1);
    cp2 = collectionPointRepository.save(cp2);

    partnerRepository.save(partner1);
    partnerRepository.save(partner2);

    parcelRepository.save(parcel);
    parcelRepository.save(parcel2);
    parcelRepository.save(parcel3);
    parcelRepository.save(parcel4);
  }

  @After
  public void tearDown() {
    driver.quit();
  }

  @Test
  public void testLogin() {
    driver.get("http://localhost:8080/main/login");
    driver.findElement(By.id("username")).sendKeys("username1");
    driver.findElement(By.id("password")).click();
    driver.findElement(By.id("password")).sendKeys("pass123");
    driver.findElement(By.cssSelector(".btn")).click();
    assertEquals(driver.getTitle(), "ACP Page");
  }
}
