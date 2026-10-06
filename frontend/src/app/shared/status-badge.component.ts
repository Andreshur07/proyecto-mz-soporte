import { Component, input } from '@angular/core';

@Component({ selector: 'app-status-badge', template: `<span class="badge" [class]="'badge ' + value().toLowerCase()">{{ value() }}</span>` })
export class StatusBadgeComponent { readonly value = input.required<string>(); }
