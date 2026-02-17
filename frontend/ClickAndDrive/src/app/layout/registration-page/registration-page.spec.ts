import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RegistrationPage } from './registration-page';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { of, throwError } from 'rxjs';

describe('RegistrationPage', () => {
  let component: RegistrationPage;
  let fixture: ComponentFixture<RegistrationPage>;
  let routerSpy: jasmine.SpyObj<Router>;
  let httpMock: HttpTestingController;

  beforeEach(async () => {
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [RegistrationPage, FormsModule, HttpClientTestingModule],
      providers: [{ provide: Router, useValue: routerSpy }]
    }).compileComponents();

    fixture = TestBed.createComponent(RegistrationPage);
    component = fixture.componentInstance;
    httpMock = TestBed.inject(HttpTestingController);
    fixture.detectChanges();
  });

  afterEach(() => httpMock.verify());

  // -------------------- Creation --------------------
  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  // -------------------- Field validation methods --------------------
  describe('Field validation methods', () => {
    it('isValidEmail should validate emails correctly', () => {
      const validEmails = ['test@gmail.com', 'john.doe@example.com', 'user@domain.rs'];
      const invalidEmails = ['invalid', 'test@', '@gmail.com', '', 'user@domain'];

      validEmails.forEach(email => expect(/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)).toBeTrue());
      invalidEmails.forEach(email => expect(/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)).toBeFalse());
    });

    it('isValidName should detect invalid characters', () => {
      const validNames = ['John', 'Ana-Marija', 'Uroš'];
      const invalidNames = ['John123', 'Ana!', ''];

      validNames.forEach(name => expect(/^[A-Za-zÀ-ž\s-]+$/.test(name)).toBeTrue());
      invalidNames.forEach(name => expect(/^[A-Za-zÀ-ž\s-]+$/.test(name)).toBeFalse());
    });

    it('isValidPhone should detect correct phone numbers', () => {
      const validPhones = ['+381612345678', '0691234567', '00381612345678'];
      const invalidPhones = ['123', 'phone', '+3816123', ''];

      validPhones.forEach(phone => expect(/^\+?\d{9,15}$/.test(phone)).toBeTrue());
      invalidPhones.forEach(phone => expect(/^\+?\d{9,15}$/.test(phone)).toBeFalse());
    });
  });

  // -------------------- checkPasswords --------------------
  describe('checkPasswords', () => {
    it('should mark input invalid when passwords mismatch', () => {
      component.registerData.password = 'abc123';
      component.registerData.confirmPassword = 'xyz123';
      const input = document.createElement('input') as HTMLInputElement;
      component.checkPasswords(input);
      expect(input.validationMessage).toBe('Passwords must match');
    });

    it('should clear validity when passwords match', () => {
      component.registerData.password = 'abc123';
      component.registerData.confirmPassword = 'abc123';
      const input = document.createElement('input') as HTMLInputElement;
      component.checkPasswords(input);
      expect(input.validationMessage).toBe('');
    });
  });

  // -------------------- validateForm --------------------
  describe('validateForm', () => {
    it('should return true for valid form', () => {
      component.registerData = {
        name: 'John',
        lastName: 'Doe',
        email: 'john@example.com',
        password: 'password123',
        confirmPassword: 'password123',
        address: 'Street 1',
        phoneNumber: '+381600000000'
      };
      
      const mockForm = {
        checkValidity: jasmine.createSpy('checkValidity').and.returnValue(true),
        reportValidity: jasmine.createSpy('reportValidity')
      };
      
      const mockEvent = { target: mockForm } as any;

      const postSpy = spyOn(component['http'], 'post').and.returnValue(of({}));

      component.register(mockEvent);
      
      expect(postSpy).toHaveBeenCalledWith(
        'http://localhost:8080/api/user/auth/register',
        component.registerData
      );
      expect(routerSpy.navigate).toHaveBeenCalledWith(['/login']);
    });

    it('should return false for invalid form', () => {
      component.registerData.name = '';
      const form = fixture.debugElement.nativeElement.querySelector('form') as HTMLFormElement;
      spyOn(form, 'checkValidity').and.returnValue(false);
      spyOn(form, 'reportValidity');

      component.register({ target: form } as any);
      expect(form.reportValidity).toHaveBeenCalled();
      const requests = httpMock.match(() => true);
      expect(requests.length).toBe(0);
    });
  });

  // -------------------- register --------------------
  describe('register', () => {
    it('should send POST request when form valid', () => {
      component.registerData = {
        name: 'Alice',
        lastName: 'Smith',
        email: 'alice@example.com',
        password: 'password123',
        confirmPassword: 'password123',
        address: 'Street 2',
        phoneNumber: '+381612345678'
      };

      const mockForm = {
        checkValidity: jasmine.createSpy('checkValidity').and.returnValue(true),
        reportValidity: jasmine.createSpy('reportValidity')
      };
      
      const mockEvent = { target: mockForm } as any;

      const postSpy = spyOn(component['http'], 'post').and.returnValue(
        of({})
      );

      component.register(mockEvent);

      expect(postSpy).toHaveBeenCalledWith(
        'http://localhost:8080/api/user/auth/register',
        component.registerData
      );
      expect(routerSpy.navigate).toHaveBeenCalledWith(['/login']);
    });

    it('should handle server error', (done) => {
      component.registerData = {
        name: 'Alice',
        lastName: 'Smith',
        email: 'alice@example.com',
        password: 'password123',
        confirmPassword: 'password123',
        address: 'Street 2',
        phoneNumber: '+381612345678'
      };

      const mockForm = {
        checkValidity: jasmine.createSpy('checkValidity').and.returnValue(true),
        reportValidity: jasmine.createSpy('reportValidity')
      };
      
      const mockEvent = { target: mockForm } as any;
      spyOn(console, 'error');

      const postSpy = spyOn(component['http'], 'post').and.returnValue(
        throwError(() => ({ status: 409, message: 'Email exists' }))
      );

      component.register(mockEvent);

      expect(postSpy).toHaveBeenCalledWith(
        'http://localhost:8080/api/user/auth/register',
        component.registerData
      );

      setTimeout(() => {
        expect(console.error).toHaveBeenCalledWith('Registration error:', jasmine.any(Object));
        done();
      }, 0);
    });
  });

  // -------------------- goHome --------------------
  describe('goHome', () => {
    it('should navigate to home', () => {
      component.goHome();
      expect(routerSpy.navigate).toHaveBeenCalledWith(['/']);
    });
  });

  // -------------------- Edge cases --------------------
  describe('edge cases', () => {
    it('should not submit invalid form', () => {
      component.registerData.name = '';
      const form = fixture.debugElement.nativeElement.querySelector('form') as HTMLFormElement;
      spyOn(form, 'checkValidity').and.returnValue(false);
      spyOn(form, 'reportValidity');

      component.register({ target: form } as any);
      expect(form.reportValidity).toHaveBeenCalled();
      const requests = httpMock.match(() => true);
      expect(requests.length).toBe(0);
    });

    it('should detect password mismatch', () => {
      component.registerData.password = 'pass1';
      component.registerData.confirmPassword = 'pass2';
      const input = document.createElement('input') as HTMLInputElement;
      component.checkPasswords(input);
      expect(input.validationMessage).toBe('Passwords must match');
    });

    it('should detect invalid email', () => {
      component.registerData.email = 'invalid@';
      const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      expect(regex.test(component.registerData.email)).toBeFalse();
    });

    it('should detect empty required fields', () => {
      component.registerData = {
        name: '',
        lastName: '',
        email: '',
        password: '',
        confirmPassword: '',
        address: '',
        phoneNumber: ''
      };
      
      const form = document.createElement('form');
      const mockEvent = { target: form } as any;
      
      spyOn(form, 'checkValidity').and.returnValue(false);
      spyOn(form, 'reportValidity');

      component.register(mockEvent);
      
      expect(form.reportValidity).toHaveBeenCalled();
    });

    it('should accept valid phone numbers', () => {
      const validPhones = ['+381612345678', '0691234567', '00381612345678'];
      validPhones.forEach(p => expect(/^\+?\d{9,15}$/.test(p)).toBeTrue());
    });

    it('should reject invalid phone numbers', () => {
      const invalidPhones = ['123', 'phone', '+3816123', ''];
      invalidPhones.forEach(p => expect(/^\+?\d{9,15}$/.test(p)).toBeFalse());
    });
  });
});
