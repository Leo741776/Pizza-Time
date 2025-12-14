# Pizza Time
This is a shoot 'em up game written in Java using JavaFX.

## Additional Library Needed to Run This Program
JavaFX: https://openjfx.io/

## Controls
Movement: arrow keys <br> Fire: spacebar

# Power Ups
Salt: increases fire rate <br> Pepper: spawns extra pizza slices which shoot alongside the player's sprite

## Classes
- BackgroundManager: handles infinite scrolling background
- CollisionManager: handles interactions between player sprite, enemy sprites, projectiles, & power ups. It also updates the score and life bar
- CollisionUtils: handles collision detection
- Enemy: handles enemy sprite visuals, movement, spawning, and firing
- EnemyProjectile: handles animation and visual of enemy projectiles
- GameManager: controls overall game logic, keeping track of player and every object on screen and handles updating everything each frame
- GameState: keeps track of player life, score, and timing information
- LifeIcon: displays life icons
- Pepper: handles pepper power up visual, spawn location, and traversal pattern
- PizzaSprite: handles player sprite visual and flash animation on interactions
- Projectile: handles projectile visual and fire() method which displays an animated projectile
- Salt: handles salt power up visual, spawn location, and traversal pattern
- SoundManager: handles various audio clips used in different interactions
- UIManager: handles UI elements such as score, instructions, logo, life, and game over screen

## Screenshots

<img width="754" height="1017" alt="Image" src="https://github.com/user-attachments/assets/4110945d-4c32-4487-81e9-aeb4038c6366" />

