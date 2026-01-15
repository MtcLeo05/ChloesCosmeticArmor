# Chloe's Cosmetic Armor

An hytale plugin that allows players to choose which armor should have a cosmetic purpose, and which should actually give you stats

> **⚠️ Warning: Early Access**    
> The game Hytale is in early access, and so is this project! Features may be
> incomplete, unstable, or change frequently. Please be patient and understanding as development
> continues.

## Main feature
This plugin adds a new item type, the Mirror. This item (with all its variants) allow you to change your cosmetic armor on the fly.
Vanish is currently still command-only, but does not require OP permission to use

To craft a mirror, simply check its recipe out in the Workbench -> Tools. All mirrors are the same, the only difference is cosmetic

## Commands

The plugin also features commands to set and remove cosmetics for testing purposes, below is a guide for the commands, but they also have their own section in hytale's built-in /help
All commands (except vanish) require OP permissions

`/cca-set-item <slot>` sets the current main hand item in a certain cosmetic slot. Available slots are: `head - chest - hands - legs`. If the item cannot be normally put in a certain slot, it won't fit, so you can't wear an helmet as pants!|

`/cca-clear-item <slot>` clears a certain slot, and restores original armor rendering, same slots available

`/cca-vanish-item <slot>` toggles a slot vanish. If a slot is vanished, it will never render armor, cosmetic nor functional