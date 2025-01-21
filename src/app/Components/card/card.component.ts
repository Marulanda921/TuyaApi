// card.component.ts
import { Component, OnInit, OnDestroy } from '@angular/core';
import { IonicModule } from '@ionic/angular';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { registerPlugin } from '@capacitor/core';
import { ToastController } from '@ionic/angular';

interface MagneticCardPlugin {
  init(): Promise<void>;
  open(): Promise<void>;
  close(): Promise<void>;
  check(options: { timeout?: number }): Promise<{
    track1: string;
    track2: string;
    track3: string;
  }>;
  startReading(): Promise<void>;
}

const MagneticCard = registerPlugin<MagneticCardPlugin>('MagneticCard');

@Component({
  selector: 'app-card',
  templateUrl: './card.component.html',
  styleUrls: ['./card.component.scss'],
  standalone: true,
  imports: [IonicModule, CommonModule, FormsModule]
})
export class CardComponent implements OnInit, OnDestroy {
  trackData: { track1: string; track2: string; track3: string } | null = null;
  isReading = false;
  loading = false;
  lastError: string | null = null;
  private readingInterval: any;

  constructor(
    private toastController: ToastController
    
  ) {}

  ngOnInit() {}

  ngOnDestroy() {
    this.stopReading();
  }

  private async showToast(message: string, color: string = 'primary') {
    const toast = await this.toastController.create({
      message,
      duration: 2000,
      color: color,
      position: 'bottom'
    });
    await toast.present();
  }

  async startSwipe() {
    this.loading = true;
    this.lastError = null;
    this.trackData = null;
    
    try {
      // Inicializar y abrir en secuencia
      await MagneticCard.init();
      await MagneticCard.open();
      await MagneticCard.startReading();
      
      this.isReading = true;
      this.startCheckingCard();
      await this.showToast('Lector listo. Deslice la tarjeta', 'success');
    } catch (error) {
      this.lastError = `Error al iniciar lectura: ${error instanceof Error ? error.message : String(error)}`;
      await this.showToast('Error al iniciar el lector', 'danger');
      this.stopReading();
    } finally {
      this.loading = false;
    }
  }

  async stopReading() {
    if (this.readingInterval) {
      clearInterval(this.readingInterval);
      this.readingInterval = null;
    }
    
    try {
      this.isReading = false;
      await MagneticCard.close();
    } catch (error) {
      console.error('Error al cerrar el lector:', error);
    }
  }

  private async startCheckingCard() {
    this.readingInterval = setInterval(async () => {
      if (!this.isReading) {
        clearInterval(this.readingInterval);
        return;
      }

      try {
        const result = await MagneticCard.check({ timeout: 50 });
        if (result && (result.track1 || result.track2 || result.track3)) {
          this.trackData = {
            track1: result.track1?.trim() || '',
            track2: result.track2?.trim() || '',
            track3: result.track3?.trim() || '',
          };
          await this.showToast('¡Tarjeta leída correctamente!', 'success');
          this.stopReading();
        }
      } catch (error) {
        // Ignorar errores de timeout
        if (error instanceof Error && !error.message.includes('timeout')) {
          console.error('Error checking card:', error);
        }
      }
    }, 100); // Check cada 100ms
  }

  clearData() {
    this.trackData = null;
  }

  clearError() {
    this.lastError = null;
  }

  
}