import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DriverRegistration } from './driver-registration';
import { DriverService } from '../../services/driver.service';
import { Router } from '@angular/router';
import { ProfileSidebarService } from '../../services/profile-sidebar.service';
import { ToastrService } from 'ngx-toastr';
import { of, throwError } from 'rxjs';

describe('DriverRegistration', () => {
  // Promenljive dostupne u svim testovima
  let component: DriverRegistration;
  let fixture: ComponentFixture<DriverRegistration>; 

  // Definisemo promenljive za nase lazne servise (spy)
  let driverServiceSpy: jasmine.SpyObj<DriverService>;
  let routerSpy: jasmine.SpyObj<Router>;
  let profileSidebarSpy: jasmine.SpyObj<ProfileSidebarService>;
  let toastrSpy: jasmine.SpyObj<ToastrService>;

  // Jedini blok koji se pokrece pre bilo kog drugog bloka
  beforeEach(async () => {
    // Kreiramo spy objekte
    driverServiceSpy = jasmine.createSpyObj('DriverService', ['registerDriver']);
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);
    profileSidebarSpy = jasmine.createSpyObj('ProfileSidebarService', ['open']);
    toastrSpy = jasmine.createSpyObj('ToastrService', ['success', 'error']);
    
    // Test okruzenje
    await TestBed.configureTestingModule({
      imports: [DriverRegistration], // Komponenta koju testiramo
      providers: [
        // Zamenjujemo prave servise spy objektima
        {provide: DriverService, useValue: driverServiceSpy},
        {provide: Router, useValue: routerSpy},
        {provide: ProfileSidebarService, useValue: profileSidebarSpy},
        {provide: ToastrService, useValue: toastrSpy}
      ]
    })
    .compileComponents(); // Kompajlira resurse komponente, templates, styles...

    // Kreiramo instancu komponente
    fixture = TestBed.createComponent(DriverRegistration);
    component = fixture.componentInstance; // Pristup komponentnoj klasi
    await fixture.whenStable();
  });

  // Proveravamo da li je komponenta kreirana
  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize formData with empty values on ngOnInit', () => {
    component.ngOnInit();

    // Proveravamo da li su driver polja inicijalizovana
    expect(component.formData['fname']).toBe('');
    expect(component.formData['lname']).toBe('');
    expect(component.formData['email']).toBe('');
    expect(component.formData['gender']).toBe('');
    expect(component.formData['address']).toBe('');
    expect(component.formData['mobile']).toBe('');

    // Sada polja vozila
    expect(component.formData['model']).toBe('');
    expect(component.formData['type']).toBe('');
    expect(component.formData['licence']).toBe('');
    expect(component.formData['seats']).toBe('');

    expect(component.formData['baby-friendly']).toBe(false);
    expect(component.formData['pet-friendly']).toBe(false);
  })


// Provera da li je unet mejl dobro formata
describe('isValidEmail', () => {

  it('should return true for valid email', () => {
    expect(component.isValidEmail('test@gmail.com')).toBe(true);
    expect(component.isValidEmail('jovan.jovanovic@demo.com')).toBe(true);
    expect(component.isValidEmail('driver@yahoo.rs')).toBe(true);
  });

  it('should return false for invalid email', () => {
    expect(component.isValidEmail('invalidemail')).toBe(false);
    expect(component.isValidEmail('test@')).toBe(false);
    expect(component.isValidEmail('@gmail.com')).toBe(false);
    expect(component.isValidEmail('test@demo')).toBe(false);
    expect(component.isValidEmail('')).toBe(false);
  });
});

// Provera da li je ime okej
describe('isValidName', () => {

  it('should return true for valid names', () => {
    expect(component.isValidName('Lazar')).toBe(true);
    expect(component.isValidName('Ana-Marija')).toBe(true);
    expect(component.isValidName('Uroš')).toBe(true);
  });

  it('should return false for invalid names', () => {
    expect(component.isValidName('Lazar123')).toBe(false);
    expect(component.isValidName('Petar#')).toBe(false);
    expect(component.isValidName('')).toBe(false);
  });
});

// Provera da li je broj mobilnog okej
describe('isValidMobile', () => {

  it('should return true for valid mobile format', () => {
    expect(component.isValidMobile('069669949')).toBe(true);
    expect(component.isValidMobile('+38169669949')).toBe(true);
  });

  it('should return false for invalid mobile format', () => {
    expect(component.isValidMobile('069432')).toBe(false);
    expect(component.isValidMobile('096699499')).toBe(false);
    expect(component.isValidMobile('069669949969')).toBe(false);
    expect(component.isValidMobile('lazar')).toBe(false);
    expect(component.isValidMobile('')).toBe(false);
  });
});

// Provera validateField metode
describe('validateField', () => {

  // Prazno polje
  it('should return false for empty when field is required', () => {
    const isValid = component.validateField('fname', '');

    expect(isValid).toBe(false);
    expect(component.fieldErrors['fname']).toBe('This field is required');
  });

  // Popunjeno polje
  it('should return true for filled required field', () => {
    const isValid = component.validateField('fname', 'Lazar');

    expect(isValid).toBe(true);
    expect(component.fieldErrors['fname']).toBeUndefined();
  });

  // Validacija imena (isto za prezime)
  it('should validate name', () => {
    // Ispravno
    expect(component.validateField('fname', 'Lazar')).toBe(true);

    // Neispravno
    const isValid = component.validateField('fname', 'Lazar123');
    expect(isValid).toBe(false);
    expect(component.fieldErrors['fname']).toBe('Field should contain letters only');
  });

  // Validacija mejla
  it('should validate email', () => {
    // Ispravno
    expect(component.validateField('email', 'topiclazar87@gmail.com')).toBe(true);

    // Neispravno
    const isValid = component.validateField('email', 'nevalidno@');
    expect(isValid).toBe(false);
    expect(component.fieldErrors['email']).toBe('Invalid email format');
  });

  // Validacija broja
  it('should validate mobile', () => {
    // Ispravno
    expect(component.validateField('mobile', '069669949')).toBe(true);

    // Neispravno
    const isValid = component.validateField('mobile', '123');
    expect(isValid).toBe(false);
    expect(component.fieldErrors['mobile']).toBe('Invalid mobile format');
  });

  // Validacija broja sedista
  it('should validate seats range', () => {
    // Ispravno
    expect(component.validateField('seats', 5)).toBe(true);
    expect(component.validateField('seats', 12)).toBe(true);

    // Neispracvno
    let isValid = component.validateField('seats', 3);
    expect(isValid).toBe(false);
    expect(component.fieldErrors['seats']).toBe('Seats must be between 4 and 12');

    isValid = component.validateField('seats', 13);
    expect(isValid).toBe(false);
    expect(component.fieldErrors['seats']).toBe('Seats must be between 4 and 12');
  });
});

// Provera validacije cele forme
describe('validateForm', () => {

  // Svi podaci dobri
  it('should return true when all fields are valid', () => {
    component.formData['fname'] = 'Lazar';
    component.formData['lname'] = 'Topic';
    component.formData['email'] = 'topiclazar87@gmail.com';
    component.formData['gender'] = 'MALE';
    component.formData['address'] = 'Nikole Tesle 47';
    component.formData['mobile'] = '069669949';
    component.formData['model'] = 'Model';
    component.formData['type'] = 'STANDARD';
    component.formData['licence'] = 'NS111PO';
    component.formData['seats'] = 5;
    component.formData['baby-friendly'] = true;
    component.formData['pet-friendly'] = false;

    const isValid = component.validateForm();
    expect(isValid).toBe(true);
    expect(component.invalidFields.length).toBe(0); // nema nevalidnih polja
    expect(Object.keys(component.fieldErrors).length).toBe(0); // nema poruka gresaka
  });

  // Polja forme prazna
  it('should return false when required fields empty', () => {
    component.ngOnInit();

    const isValid = component.validateForm();
    expect(isValid).toBe(false);
    expect(component.invalidFields.length).toBeGreaterThan(0);

    expect(component.invalidFields).toContain('fname');
    expect(component.invalidFields).toContain('lname');
    expect(component.invalidFields).toContain('email');
  });

  // Neka polja nevalidna
  it('should return false for invalid fields', () => {
    component.formData['fname'] = 'Lazar';
    component.formData['lname'] = 'Topic!';
    component.formData['email'] = '@gmail.com';
    component.formData['gender'] = 'MALE';
    component.formData['address'] = 'Nikole Tesle';
    component.formData['mobile'] = '';
    component.formData['model'] = 'Model';
    component.formData['type'] = 'STANDARD';
    component.formData['licence'] = 'NS123KK';
    component.formData['seats'] = '13';

    const isValid = component.validateForm();
    expect(isValid).toBe(false);
    
    expect(component.invalidFields).toContain('fname');
    expect(component.invalidFields).toContain('email');
    expect(component.invalidFields).toContain('mobile');
      
    expect(component.fieldErrors['fname']).toBe('Field should contain letters only');
    expect(component.fieldErrors['email']).toBe('Invalid email format');
    expect(component.fieldErrors['mobile']).toBe('Invalid mobile format');
  });
});

// Provera isFieldInvalid metode
describe('isFieldInvalid', () => {

  it('should return true if field is in invalidFields list', () => {
    component.invalidFields = ['fname', 'email'];

    expect(component.isFieldInvalid('fname')).toBe(true);
    expect(component.isFieldInvalid('email')).toBe(true);
    expect(component.isFieldInvalid('lname')).toBe(false);
  });
});

// Test buildDriver metode
// Metoda kreira vozaca na osnovu podataka iz forme
describe('buildDriver', () => {

  it('should build correct driver object', () => {
    component.formData['fname'] = 'Lazar';
    component.formData['lname'] = 'Topic';
    component.formData['email'] = 'ltopic764@gmail.com';
    component.formData['gender'] = 'MALE';
    component.formData['address'] = 'Nikole Tesle';
    component.formData['mobile'] = '0612345678';
    component.formData['model'] = 'Model';
    component.formData['type'] = 'STANDARD';
    component.formData['licence'] = 'NS123KK';
    component.formData['seats'] = '5';
    component.formData['baby-friendly'] = true;
    component.formData['pet-friendly'] = false;

    // Poziv metode
    const driver = component['buildDriver']();

    // Provera objekta
    expect(driver.name).toBe('Lazar');
    expect(driver.surname).toBe('Topic');
    expect(driver.email).toBe('ltopic764@gmail.com');
    expect(driver.gender).toBe('MALE');
    expect(driver.address).toBe('Nikole Tesle');
    expect(driver.phone).toBe('0612345678');

    expect(driver.vehicle.model).toBe('Model');
    expect(driver.vehicle.type).toBe('STANDARD');
    expect(driver.vehicle.registration).toBe('NS123KK');
    expect(driver.vehicle.seats).toBe(5);
    expect(driver.vehicle.babyFriendly).toBe(true);
    expect(driver.vehicle.petFriendly).toBe(false);
  });
});

// Test registerDriver
describe('registerDriver', () => {

  it('should call DriverService.registerDriver with correct data', () => {
    // Popuni formu dobrim podacima
    component.formData['fname'] = '';
    component.formData['fname'] = 'Lazar';
    component.formData['lname'] = 'Topic';
    component.formData['email'] = 'ltopic764@gmail.com';
    component.formData['gender'] = 'MALE';
    component.formData['address'] = 'Nikole Tesle';
    component.formData['mobile'] = '0612345678';
    component.formData['model'] = 'Model';
    component.formData['type'] = 'STANDARD';
    component.formData['licence'] = 'NS123KK';
    component.formData['seats'] = '5';
    component.formData['baby-friendly'] = true;
    component.formData['pet-friendly'] = false;

    // Simuliramo uspesan odgovor
    const mockResponse = {id: 1, email: 'ltopic764@gmail.com', name: 'Lazar', surname: 'Topic', status: 'ACTIVE'};
    driverServiceSpy.registerDriver.and.returnValue(of(mockResponse as any));

    routerSpy.navigate.and.returnValue(Promise.resolve(true));

    // Poziv metode
    component.registerDriver();

    expect(driverServiceSpy.registerDriver).toHaveBeenCalled();

    expect(driverServiceSpy.registerDriver).toHaveBeenCalledWith(
      jasmine.objectContaining({
        name: 'Lazar',
        surname: 'Topic',
        email: 'ltopic764@gmail.com',
        gender: 'MALE',
        address: 'Nikole Tesle',
        phone: '0612345678',
        vehicle: jasmine.objectContaining({
          model: 'Model',
            type: 'STANDARD',
            registration: 'NS123KK',
            seats: 5,
            babyFriendly: true,
            petFriendly: false
        })
      })
    );

    expect(toastrSpy.success).toHaveBeenCalledWith(
      'Driver registered successfully', 'Success'
    );
  });

  // Forma koja ne valjda ne poziva servis
  it('should not call DriverService when form is not valid', () => {
    component.ngOnInit();

    component.registerDriver();

    // Servis ne bi trebao biti pozvan
    expect(driverServiceSpy.registerDriver).not.toHaveBeenCalled();

    expect(toastrSpy.error).toHaveBeenCalledWith(
      'Please fill in all required fields correctly', 'Validation Error'
    );
  });


  // Greska pri registraciji
  it('should show error when registration fails', () => {
    // Popuni formu
    component.formData['fname'] = '';
    component.formData['fname'] = 'Lazar';
    component.formData['lname'] = 'Topic';
    component.formData['email'] = 'ltopic764@gmail.com';
    component.formData['gender'] = 'MALE';
    component.formData['address'] = 'Nikole Tesle';
    component.formData['mobile'] = '0612345678';
    component.formData['model'] = 'Model';
    component.formData['type'] = 'STANDARD';
    component.formData['licence'] = 'NS123KK';
    component.formData['seats'] = '5';
    component.formData['baby-friendly'] = true;
    component.formData['pet-friendly'] = false;

    // Greska sa servera
    const errorResponse = {error: {message: 'Email already exists'}};
    driverServiceSpy.registerDriver.and.returnValue(throwError(() => errorResponse));
    
    component.registerDriver();

    expect(toastrSpy.error).toHaveBeenCalledWith(
      'Email already exists', 'Error'
    );
  });
});

describe('closeRegistrationForm', () => {
  it('should navigate to home and open profile sidebar', () => {
    routerSpy.navigate.and.returnValue(Promise.resolve(true));

    component.closeRegistrationForm();

    expect(routerSpy.navigate).toHaveBeenCalledWith(['/']);
    
    fixture.whenStable().then(() => {
      expect(profileSidebarSpy.open).toHaveBeenCalled();
    });
  });
});
});

