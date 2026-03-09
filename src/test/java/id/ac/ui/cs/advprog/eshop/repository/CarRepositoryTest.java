package id.ac.ui.cs.advprog.eshop.repository;

import id.ac.ui.cs.advprog.eshop.model.Car;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

class CarRepositoryTest {

    private CarRepository carRepository;

    @BeforeEach
    void setUp() {
        carRepository = new CarRepository();
    }

    @Test
    void testCreateCarWithNullId() {
        Car car = new Car();
        car.setCarName("Toyota");
        car.setCarColor("Red");
        car.setCarQuantity(10);

        Car result = carRepository.create(car);

        assertNotNull(result.getCarId());
        assertEquals("Toyota", result.getCarName());
        assertEquals("Red", result.getCarColor());
        assertEquals(10, result.getCarQuantity());
    }

    @Test
    void testCreateCarWithExistingId() {
        Car car = new Car();
        car.setCarId("existing-id");
        car.setCarName("Honda");
        car.setCarColor("Blue");
        car.setCarQuantity(5);

        Car result = carRepository.create(car);

        assertEquals("existing-id", result.getCarId());
        assertEquals("Honda", result.getCarName());
    }

    @Test
    void testFindAll() {
        Car car1 = new Car();
        car1.setCarName("Car1");
        Car car2 = new Car();
        car2.setCarName("Car2");

        carRepository.create(car1);
        carRepository.create(car2);

        Iterator<Car> iterator = carRepository.findAll();
        int count = 0;
        while (iterator.hasNext()) {
            iterator.next();
            count++;
        }

        assertEquals(2, count);
    }

    @Test
    void testFindByIdFound() {
        Car car = new Car();
        car.setCarName("FindMe");
        carRepository.create(car);

        Car found = carRepository.findById(car.getCarId());

        assertNotNull(found);
        assertEquals("FindMe", found.getCarName());
    }

    @Test
    void testFindByIdNotFound() {
        Car result = carRepository.findById("non-existent-id");

        assertNull(result);
    }

    @Test
    void testFindByIdNotFoundWhenRepositoryHasData() {
        Car car = new Car();
        car.setCarId("car-1");
        car.setCarName("ExistingCar");
        carRepository.create(car);

        Car result = carRepository.findById("another-id");

        assertNull(result);
    }

    @Test
    void testUpdateFound() {
        Car car = new Car();
        car.setCarName("Original");
        car.setCarColor("White");
        car.setCarQuantity(1);
        carRepository.create(car);

        Car updatedCar = new Car();
        updatedCar.setCarName("Updated");
        updatedCar.setCarColor("Black");
        updatedCar.setCarQuantity(99);

        Car result = carRepository.update(car.getCarId(), updatedCar);

        assertNotNull(result);
        assertEquals("Updated", result.getCarName());
        assertEquals("Black", result.getCarColor());
        assertEquals(99, result.getCarQuantity());
    }

    @Test
    void testUpdateNotFound() {
        Car updatedCar = new Car();
        updatedCar.setCarName("Updated");

        Car result = carRepository.update("non-existent-id", updatedCar);

        assertNull(result);
    }

    @Test
    void testUpdateNotFoundWhenRepositoryHasData() {
        Car existingCar = new Car();
        existingCar.setCarId("car-2");
        existingCar.setCarName("Existing");
        carRepository.create(existingCar);

        Car updatedCar = new Car();
        updatedCar.setCarName("Updated");

        Car result = carRepository.update("unknown-id", updatedCar);

        assertNull(result);
        assertEquals("Existing", carRepository.findById("car-2").getCarName());
    }

    @Test
    void testDelete() {
        Car car = new Car();
        car.setCarName("ToDelete");
        carRepository.create(car);
        String carId = car.getCarId();

        carRepository.delete(carId);

        assertNull(carRepository.findById(carId));
    }

    @Test
    void testDeleteIfIdNotFoundShouldNotRemoveExistingCar() {
        Car car = new Car();
        car.setCarName("StillHere");
        carRepository.create(car);

        carRepository.delete("non-existent-id");

        assertNotNull(carRepository.findById(car.getCarId()));
    }
}
