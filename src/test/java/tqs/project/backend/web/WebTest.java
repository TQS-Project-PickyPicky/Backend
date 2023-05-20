package tqs.project.backend.web;

import org.openqa.selenium.firefox.FirefoxDriver;
import tqs.project.backend.data.collection_point.CollectionPoint;
import tqs.project.backend.data.collection_point.CollectionPointRepository;
import tqs.project.backend.data.parcel.Parcel;
import tqs.project.backend.data.parcel.ParcelRepository;
import tqs.project.backend.data.parcel.ParcelStatus;
import tqs.project.backend.data.store.Store;
import tqs.project.backend.data.store.StoreRepository;
import tqs.project.backend.web.pages.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebTest {

    @LocalServerPort
    int localPort;
    private WebDriver driver;

    @Autowired
    private CollectionPointRepository collectionPointRepository;

    @Autowired
    private ParcelRepository parcelRepository;

    @Autowired
    private StoreRepository storeRepository;

    @BeforeEach
    public void setup() {
        driver = new FirefoxDriver();

        collectionPointRepository.deleteAll();
        storeRepository.deleteAll();
        parcelRepository.deleteAll();

        CollectionPoint collectionPoint = new CollectionPoint();
        collectionPoint.setName("Collection Point 1");
        collectionPoint.setType("Collection Point");
        collectionPoint.setCapacity(100);
        collectionPoint.setAddress("Rua do ISEP");
        collectionPoint.setLatitude(41.178);
        collectionPoint.setLongitude(-8.608);
        collectionPoint.setOwnerName("João");
        collectionPoint.setOwnerEmail("joao@ua.pt");
        collectionPoint.setOwnerGender("M");
        collectionPoint.setOwnerPhone(123456789);
        collectionPoint.setOwnerMobilePhone(987654321);

        Store store = new Store();
        store.setName("Store 1");

        Parcel parcel = new Parcel();
        parcel.setToken(123456);
        parcel.setClientName("João");
        parcel.setClientEmail("joao@ua.pt");
        parcel.setClientPhone(123456789);
        parcel.setClientMobilePhone(987654321);
        parcel.setExpectedArrival(LocalDate.now().plusDays(5));
        parcel.setStore(store);
        parcel.setStatus(ParcelStatus.IN_TRANSIT);
        parcel.setCollectionPoint(collectionPoint);

        Parcel parcel2 = new Parcel();
        parcel2.setToken(123456);
        parcel2.setClientName("Jorge");
        parcel2.setClientEmail("jorge@ua.pt");
        parcel2.setClientPhone(123456789);
        parcel2.setClientMobilePhone(987654321);
        parcel2.setExpectedArrival(LocalDate.now().plusDays(7));
        parcel2.setStore(store);
        parcel2.setStatus(ParcelStatus.DELIVERED);
        parcel2.setCollectionPoint(collectionPoint);

        Parcel parcel3 = new Parcel();
        parcel3.setToken(123456);
        parcel3.setClientName("Gabriel");
        parcel3.setClientEmail("gabriel@ua.pt");
        parcel3.setClientPhone(123456789);
        parcel3.setClientMobilePhone(987654321);
        parcel3.setExpectedArrival(LocalDate.now());
        parcel3.setStore(store);
        parcel3.setStatus(ParcelStatus.COLLECTED);
        parcel3.setCollectionPoint(collectionPoint);

        Parcel parcel4 = new Parcel();
        parcel4.setToken(123456);
        parcel4.setClientName("Tiago");
        parcel4.setClientEmail("tiago@ua.pt");
        parcel4.setClientPhone(123456789);
        parcel4.setClientMobilePhone(987654321);
        parcel4.setExpectedArrival(LocalDate.now().plusDays(3));
        parcel4.setStore(store);
        parcel4.setStatus(ParcelStatus.RETURNED);
        parcel4.setCollectionPoint(collectionPoint);

        List<Parcel> parcels = new ArrayList<>();
        parcels.add(parcel);
        parcels.add(parcel2);
        parcels.add(parcel3);
        parcels.add(parcel4);

        //Save
        CollectionPoint acp = collectionPointRepository.save(collectionPoint);
        storeRepository.save(store);
        parcelRepository.saveAll(parcels);

        String url = "http://localhost:" + localPort + "/acp?id=" + acp.getId();

        driver.get(url);
    }

    @AfterEach
    public void teardown() {
        driver.quit();
    }

    @Test
    public void inTransitTest() {
        ACPPage acpPage = new ACPPage(driver);
        assertThat(acpPage.isParcelTableDisplayed()).isTrue();
        acpPage.clickInTransitButton();

        ParcelPage parcelPage = new ParcelPage(driver);
        assertThat(parcelPage.getStatus()).isEqualTo("In Transit");
        assertThat(parcelPage.isActionButtonDisplayed()).isTrue();
        assertThat(parcelPage.getAction()).isEqualTo("Check-In");
    }

    @Test
    public void deliveredTest() {
        ACPPage acpPage = new ACPPage(driver);
        assertThat(acpPage.isParcelTableDisplayed()).isTrue();
        acpPage.clickDeliveredButton();

        ParcelDPage parcelPage = new ParcelDPage(driver);
        assertThat(parcelPage.getStatus()).isEqualTo("Delivered");
        assertThat(parcelPage.isActionButtonDisplayed()).isTrue();
        assertThat(parcelPage.getAction()).isEqualTo("Check-Out");
    }

    @Test
    public void collectedTest() {
        ACPPage acpPage = new ACPPage(driver);
        assertThat(acpPage.isParcelTableDisplayed()).isTrue();
        acpPage.clickCollectedButton();

        ParcelCPage parcelPage = new ParcelCPage(driver);
        assertThat(parcelPage.getStatus()).isEqualTo("Collected");
        assertThat(parcelPage.isActionButtonDisplayed()).isTrue();
        assertThat(parcelPage.getAction()).isEqualTo("Return");
    }

    @Test
    public void returnedTest() {
        ACPPage acpPage = new ACPPage(driver);
        assertThat(acpPage.isParcelTableDisplayed()).isTrue();
        acpPage.clickReturnedButton();

        ParcelRPage parcelPage = new ParcelRPage(driver);
        assertThat(parcelPage.getStatus()).isEqualTo("Returned");
    }
}
