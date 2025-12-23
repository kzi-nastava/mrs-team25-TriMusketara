import { bootstrapApplication } from '@angular/platform-browser';
import { provideRouter } from '@angular/router';

import { App } from './app/app';
import { PublicLayout } from './app/layout/public-layout/public-layout';
import { MainPageComponent } from './app/main-page/main-page';
import { LoginPage } from './app/layout/login-page/login-page';
import { RegistrationPage } from './app/layout/registration-page/registration-page';

bootstrapApplication(App, {
  providers: [
    provideRouter([
      {
        path: '',
        component: PublicLayout,
        children: [
          { path: '', component: MainPageComponent }
        ]
      },
      {
        path: 'login',
        component: LoginPage
      },
      {
        path: 'register',
        component: RegistrationPage
      }
    ])
  ]
}).catch(err => console.error(err));
