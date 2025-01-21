import { Component } from '@angular/core';
import { AlertController } from '@ionic/angular';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { IonicModule } from '@ionic/angular';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

@Component({
  selector: 'app-pin',
  templateUrl: './pin.component.html',
  styleUrls: ['./pin.component.scss'],
  standalone: true,
  imports: [CommonModule, FormsModule, IonicModule],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class PinComponent {
  pin: string = '';

  constructor(private readonly alertController: AlertController) {}

  async submitPin() {
    if (this.pin.length !== 4) {
      const alert = await this.alertController.create({
        header: 'Invalid PIN',
        message: 'Please enter a 4-digit PIN.',
        buttons: ['OK']
      });
      await alert.present();
      return;
    }

    console.log('PIN submitted:', this.pin);

    const alert = await this.alertController.create({
      header: 'PIN Submitted',
      message: `You entered: ${this.pin}`,
      buttons: ['OK']
    });
    await alert.present();
  }

  handleKeyDown(event: KeyboardEvent) {
    if (event.key === 'Enter') {
      this.submitPin();
    }
  }
}