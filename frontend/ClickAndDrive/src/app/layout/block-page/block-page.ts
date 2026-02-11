import { Component } from '@angular/core';

@Component({
  selector: 'app-block-page',
  imports: [],
  templateUrl: './block-page.html',
  styleUrl: './block-page.css',
})
export class BlockPage {

  fields = [
    {name: 'Lazar', lastname: 'Topic'},
    {name: 'Alek', lastname: 'Cvetkovic'},
    {name: 'Uros', lastname: 'Uzelac'}
  ]
}
