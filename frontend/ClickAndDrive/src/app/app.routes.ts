import { Routes } from '@angular/router';
import { MainPageComponent } from './main-page/main-page';
import { ChangeInfoPage } from './layout/change-info-page/change-info-page';

export const routes: Routes = [
    {
        path: '',
        component: MainPageComponent
    },
    {
        path: 'change-information-page',
        component: ChangeInfoPage
    }
];
