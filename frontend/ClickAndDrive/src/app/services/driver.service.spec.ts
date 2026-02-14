import { HttpClientTestingModule, HttpTestingController } from "@angular/common/http/testing";
import { DriverService } from "./driver.service";
import { TestBed } from "@angular/core/testing";
import { DriverCreate } from "./models/driver-create";
import { Driver } from "./models/driver-reg-response";

describe('DriverService', () => {
    // Promenljive dostupne u svim testovima
    let service: DriverService;
    let httpMock: HttpTestingController;

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],

            providers: [DriverService]
        });

        service = TestBed.inject(DriverService);
        httpMock = TestBed.inject(HttpTestingController);
    });

    afterEach(() => {
        // .verify proverava da li su svi ocekivani http zahtevi poslati
        httpMock.verify();
    });


    it('should be created', () => {
        expect(service).toBeTruthy();
    }); 

// Registracija vozaca
describe('registerDriver', () => {

    // Uspesna registracija
    it('should send POST request with correct data', () => {
        // Mock objekat koji se salje
        const mockDriverData: DriverCreate = {
            name: 'Lazar',
            surname: 'Topic',
            email: 'ltopic764@gmail.com',
            gender: 'MALE',
            address: 'Nikole Tesle',
            phone: '0612345678',
            vehicle: {
                model: 'Model',
                type: 'STANDARD',
                registration: 'NS123KK',
                seats: 5,
                babyFriendly: true,
                petFriendly: false
            }
        };

        // Mock odgovor koji dobijamo
        const mockResponse: Driver = {
            id: 1,
            email: 'ltopic764@gmail.com',
            name: 'Lazar',
            surname: 'Toppic',
            status: 'ACTIVE'
        };

        // Pozivamo metodu servisa
        service.registerDriver(mockDriverData).subscribe({
            next: (response) => {
                // Provera ispravnosti odgovora
                expect(response).toEqual(mockResponse);
                expect(response.id).toBe(1);
                expect(response.email).toBe('ltopic764@gmail.com');
                expect(response.name).toBe('Lazar');
            },
            error: () => {
                fail('Expected successful response, but got error'); // test pada
            }
        });

        // Provera http zahteva
        const req = httpMock.expectOne('http://localhost:8080/api/admin/drivers');

        expect(req.request.method).toBe('POST');

        expect(req.request.body).toEqual(mockDriverData);

        // Flush salje mock odgovor kroz observable
        req.flush(mockResponse);
    });

    // Proces registracije pao
    it('should handle error when process fails', () => {
        // Mock objekat koji se salje
        const mockDriverData: DriverCreate = {
            name: 'Lazar',
            surname: 'Topic',
            email: 'ltopic764@gmail.com',
            gender: 'MALE',
            address: 'Nikole Tesle',
            phone: '0612345678',
            vehicle: {
                model: 'Model',
                type: 'STANDARD',
                registration: 'NS123KK',
                seats: 5,
                babyFriendly: true,
                petFriendly: false
            }
        };

        const mockErrorMessage = 'Email already exists';

        service.registerDriver(mockDriverData).subscribe({
            next: () => {
                // Ocekujemo da ce test pasti, ne proci
                fail('Expected error, but got success');
            },
            error: (error) => {
                expect(error).toBeTruthy();

                expect(error.status).toBe(400);

                expect(error.error.message).toBe(mockErrorMessage);
            }
        });

        const req = httpMock.expectOne('http://localhost:8080/api/admin/drivers');

        expect(req.request.method).toBe('POST');

        // Simuliramo error odgovor
        req.flush(
            {message: mockErrorMessage}, // body
            {status: 400, statusText: 'Bad Request'} //status
        );
    });

    // Greska sa serverom
    it('should handle server error (500)', () => {
        // Mock objekat koji se salje
        const mockDriverData: DriverCreate = {
            name: 'Lazar',
            surname: 'Topic',
            email: 'ltopic764@gmail.com',
            gender: 'MALE',
            address: 'Nikole Tesle',
            phone: '0612345678',
            vehicle: {
                model: 'Model',
                type: 'STANDARD',
                registration: 'NS123KK',
                seats: 5,
                babyFriendly: true,
                petFriendly: false
            }
        };

        service.registerDriver(mockDriverData).subscribe({
            next: () => fail('Expected error'),
            error: (error) => {
                // provera status broja
                expect(error.status).toBe(500);
                expect(error.statusText).toBe('Internal Server Error');
            }
        });

        const req = httpMock.expectOne('http://localhost:8080/api/admin/drivers');

        req.flush(
            {message: 'Something went wrong'},
            {status: 500, statusText: 'Internal Server Error'}
        )
    });

    // Provera strukture vozila
    it('should send vehicle data in correct form', () => {
        // Mock objekat koji se salje
        const mockDriverData: DriverCreate = {
            name: 'Lazar',
            surname: 'Topic',
            email: 'ltopic764@gmail.com',
            gender: 'MALE',
            address: 'Nikole Tesle',
            phone: '0612345678',
            vehicle: {
                model: 'Model',
                type: 'STANDARD',
                registration: 'NS123KK',
                seats: 5,
                babyFriendly: true,
                petFriendly: false
            }
        };

        service.registerDriver(mockDriverData).subscribe();

        const req = httpMock.expectOne('http://localhost:8080/api/admin/drivers');

        expect(req.request.body.vehicle).toBeDefined();
        expect(req.request.body.vehicle.model).toBe('Model');
        expect(req.request.body.vehicle.type).toBe('STANDARD');
        expect(req.request.body.vehicle.registration).toBe('NS123KK');
        expect(req.request.body.vehicle.seats).toBe(5);
        expect(req.request.body.vehicle.babyFriendly).toBe(true);
        expect(req.request.body.vehicle.petFriendly).toBe(false);

        // Mock odgovor koji dobijamo
        const mockResponse: Driver = {
            id: 1,
            email: 'ltopic764@gmail.com',
            name: 'Lazar',
            surname: 'Toppic',
            status: 'ACTIVE'
        };

        req.flush(mockResponse);
    });

    // Provera da li se salje samo jedan zahtev
    it('should send only one request per process', () => {
         // Mock objekat koji se salje
         const mockDriverData: DriverCreate = {
            name: 'Lazar',
            surname: 'Topic',
            email: 'ltopic764@gmail.com',
            gender: 'MALE',
            address: 'Nikole Tesle',
            phone: '0612345678',
            vehicle: {
                model: 'Model',
                type: 'STANDARD',
                registration: 'NS123KK',
                seats: 5,
                babyFriendly: true,
                petFriendly: false
            }
        };

        service.registerDriver(mockDriverData).subscribe();

        const req = httpMock.match('http://localhost:8080/api/admin/drivers');
        expect(req.length).toBe(1);

        // Mock odgovor koji dobijamo
        const mockResponse: Driver = {
            id: 1,
            email: 'ltopic764@gmail.com',
            name: 'Lazar',
            surname: 'Toppic',
            status: 'ACTIVE'
        };

        req[0].flush(mockResponse);
    });
});
});
